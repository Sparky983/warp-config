package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Map;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class MapDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;
  Deserializer<Map<String, String>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer =
        Deserializers.map(
            (node, context) -> {
              try {
                return Renderer.of("key: " + Integer.valueOf(node.asString()));
              } catch (final NumberFormatException e) {
                throw new DeserializationException(ConfigurationError.error("Cannot parse"));
              }
            },
            (node, context) -> Renderer.of("value: " + node.asInteger()));
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testMap_NullKeyDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.map(Deserializers.STRING, null));
  }

  @Test
  void testMap_NullValueDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.map(null, Deserializers.STRING));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.map();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonMap() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a map")), thrown.errors());
  }

  @Test
  void testDeserialize_NestedNonDeserializable() {
    final ConfigurationNode node =
        ConfigurationNode.map(
            Map.entry("1", ConfigurationNode.nil()),
            Map.entry("not integer", ConfigurationNode.nil()));

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(
        List.of(
            ConfigurationError.group("1", ConfigurationError.error("Must be an integer")),
            ConfigurationError.group(
                "not integer",
                ConfigurationError.error("Cannot parse"),
                ConfigurationError.error("Must be an integer"))),
        thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map(
            Map.of(
                "1", ConfigurationNode.integer(2),
                "3", ConfigurationNode.integer(4)));

    final Renderer<Map<String, String>> renderer =
        deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_NullNode() throws DeserializationException {
    final Map<String, String> result =
        deserializer.deserialize(null, deserializerContext).render(rendererContext);

    assertEquals(Map.of(), result);
  }

  @Test
  void testRender() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map(
            Map.of(
                "1", ConfigurationNode.integer(2),
                "3", ConfigurationNode.integer(4)));

    final Map<String, String> result =
        deserializer.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(Map.of("key: 1", "value: 2", "key: 3", "value: 4"), result);
    assertThrows(UnsupportedOperationException.class, () -> result.put("key: 1", "value: 2"));
  }
}
