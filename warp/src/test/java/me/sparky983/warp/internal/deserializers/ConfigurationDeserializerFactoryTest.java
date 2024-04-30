package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class ConfigurationDeserializerFactoryTest {
  @Mock DefaultsRegistry defaults;
  @Mock DeserializerRegistry deserializers;
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  DeserializerFactory factory;

  @BeforeEach
  void setUp() {
    factory = new ConfigurationDeserializerFactory(defaults);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(defaults, deserializers, deserializerContext, rendererContext);
  }

  @Test
  void testGet_NullType() {
    assertThrows(NullPointerException.class, () -> factory.create(deserializers, null));
  }

  @Test
  void testGet_NotAnnotated() {
    assertEquals(
        Optional.empty(),
        factory.create(
            deserializers, ParameterizedType.of(Configurations.MissingAnnotation.class)));
  }

  @Test
  void testGet_Invalid() {
    assertThrows(
        IllegalStateException.class,
        () -> factory.create(deserializers, ParameterizedType.of(Configurations.Sealed.class)));
  }

  @Test
  void testDeserialize_NullNode() {
    final Deserializer<? extends Configurations.Empty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    assertThrows(
        NullPointerException.class, () -> deserializer.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final Deserializer<? extends Configurations.Empty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonMap() {
    final Deserializer<? extends Configurations.Empty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a map")), thrown.errors());
  }

  @Test
  void testDeserialize_PropertyHasErrorDuringDeserialization(
      @Mock final Deserializer<String> stringDeserializer) {
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(stringDeserializer));

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.map();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "property", ConfigurationError.error("Must be set to a value"))),
        thrown.errors());

    verify(defaults).get(String.class);
    verify(deserializers).get(ParameterizedType.of(String.class));
  }

  @Test
  void testDeserialize_PropertyHasNoDeserializer() {
    when(deserializers.get(ParameterizedType.of(String.class))).thenReturn(Optional.empty());

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    // as long as the node is a map, errors about deserializers will occur before ones related to
    // the node
    final ConfigurationNode node = ConfigurationNode.map();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    verify(deserializers).get(ParameterizedType.of(String.class));
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final Deserializer<? extends Configurations.Empty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final Renderer<? extends Configurations.Empty> renderer =
        deserializer.deserialize(ConfigurationNode.map(), deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_DefaultsFromParent() throws DeserializationException {
    when(defaults.get(Optional.class)).thenReturn(Optional.of(ConfigurationNode.nil()));
    when(deserializers.get(ParameterizedType.of(Optional.class, String.class)))
        .thenReturn(
            Optional.of(
                (node, context) -> {
                  assertEquals(node, ConfigurationNode.nil());
                  return Renderer.of(Optional.empty());
                }));

    final Deserializer<? extends Configurations.StringOptional> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.StringOptional.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.map();

    final Renderer<? extends Configurations.StringOptional> renderer =
        deserializer.deserialize(node, deserializerContext);

    final Configurations.StringOptional stringOptional = renderer.render(rendererContext);

    assertEquals(Optional.empty(), stringOptional.property());
  }

  @Test
  void testRender() throws DeserializationException {
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(
            Optional.of(
                (node, context) -> {
                  assertEquals(node, ConfigurationNode.string("value"));
                  return Renderer.of("value");
                }));

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node =
        ConfigurationNode.map(Map.of("property", ConfigurationNode.string("value")));

    final Renderer<? extends Configurations.String> renderer =
        deserializer.deserialize(node, deserializerContext);

    final Configurations.String string = renderer.render(rendererContext);

    assertEquals("value", string.property());
  }
}
