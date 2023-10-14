package me.sparky983.warp.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class ConfigurationDeserializerRegistryTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  DeserializerRegistry registry;

  @BeforeEach
  void setUp() {
    registry = new ConfigurationDeserializerRegistry();
  }

  @Test
  void testGet_NullType() {
    assertThrows(NullPointerException.class, () -> registry.get(null));
  }

  @Test
  void testGet_NotAnnotated() {
    assertEquals(
        Optional.empty(),
        registry.get(ParameterizedType.of(Configurations.MissingAnnotation.class)));
  }

  @Test
  void testGet_Invalid() {
    assertThrows(
        IllegalStateException.class,
        () -> registry.get(ParameterizedType.of(Configurations.Sealed.class)));
  }

  @Test
  void testDeserialize_NullNode() {
    final Deserializer<Configurations.Empty> deserializer =
        registry
            .get(ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    assertThrows(
        NullPointerException.class, () -> deserializer.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final Deserializer<Configurations.Empty> deserializer =
        registry
            .get(ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonMap() {
    final Deserializer<Configurations.Empty> deserializer =
        registry
            .get(ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a map")), thrown.errors());
  }

  @Test
  void testDeserialize_NestedNonDeserializable() {
    final Deserializer<Configurations.String> deserializer =
        registry
            .get(ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.map().build();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "property", ConfigurationError.error("Must be set to a value"))),
        thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final Deserializer<Configurations.Empty> deserializer =
        registry
            .get(ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final Renderer<Configurations.Empty> renderer =
        deserializer.deserialize(ConfigurationNode.map().build(), deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender() throws DeserializationException {
    final Deserializer<Configurations.String> deserializer =
        registry
            .get(ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node =
        ConfigurationNode.map().entry("property", ConfigurationNode.string("value")).build();

    final Renderer<Configurations.String> renderer =
        deserializer.deserialize(node, deserializerContext);

    final Configurations.String string = renderer.render(rendererContext);

    assertEquals("value", string.property());
  }
}
