package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
  @Mock Deserializer<Integer> keyDeserializer;
  @Mock Deserializer<Integer> valueDeserializer;
  Deserializer<Map<Integer, Integer>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.map(keyDeserializer, valueDeserializer);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(
        deserializerContext, rendererContext, keyDeserializer, valueDeserializer);
  }

  @Test
  void testMap_NullKeyDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.map(keyDeserializer, null));
  }

  @Test
  void testMap_NullValueDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.map(null, valueDeserializer));
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
  void testDeserialize_NestedNonDeserializable(@Mock final Renderer<Integer> validRenderer)
      throws DeserializationException {
    when(keyDeserializer.deserialize(ConfigurationNode.string("1"), deserializerContext))
        .thenReturn(validRenderer);
    when(keyDeserializer.deserialize(ConfigurationNode.string("not integer"), deserializerContext))
        .thenThrow(new DeserializationException(ConfigurationError.error("Cannot parse")));
    when(valueDeserializer.deserialize(ConfigurationNode.nil(), deserializerContext))
        .thenThrow(new DeserializationException(ConfigurationError.error("Must be an integer")));

    final ConfigurationNode node =
        ConfigurationNode.map(
            Map.entry("1", ConfigurationNode.nil()),
            Map.entry("not integer", ConfigurationNode.nil()));

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));
    verify(keyDeserializer).deserialize(ConfigurationNode.string("1"), deserializerContext);
    verify(keyDeserializer)
        .deserialize(ConfigurationNode.string("not integer"), deserializerContext);
    verify(valueDeserializer, times(2)).deserialize(ConfigurationNode.nil(), deserializerContext);
    assertIterableEquals(
        List.of(
            ConfigurationError.group("1", ConfigurationError.error("Must be an integer")),
            ConfigurationError.group(
                "not integer",
                ConfigurationError.error("Cannot parse"),
                ConfigurationError.error("Must be an integer"))),
        thrown.errors());
    verifyNoMoreInteractions(validRenderer);
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.map();

    final Renderer<Map<Integer, Integer>> renderer =
        deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_NullNode() throws DeserializationException {
    final Map<Integer, Integer> result =
        deserializer.deserialize(null, deserializerContext).render(rendererContext);

    assertEquals(Map.of(), result);
  }

  @Test
  void testRender(
      final @Mock Renderer<Integer> key1Renderer,
      final @Mock Renderer<Integer> value1Renderer,
      final @Mock Renderer<Integer> key2Renderer,
      final @Mock Renderer<Integer> value2Renderer)
      throws DeserializationException {
    when(keyDeserializer.deserialize(ConfigurationNode.string("1"), deserializerContext))
        .thenReturn(key1Renderer);
    when(key1Renderer.render(rendererContext)).thenReturn(1);
    when(valueDeserializer.deserialize(ConfigurationNode.integer(2), deserializerContext))
        .thenReturn(value1Renderer);
    when(value1Renderer.render(rendererContext)).thenReturn(2);
    when(keyDeserializer.deserialize(ConfigurationNode.string("3"), deserializerContext))
        .thenReturn(key2Renderer);
    when(key2Renderer.render(rendererContext)).thenReturn(3);
    when(valueDeserializer.deserialize(ConfigurationNode.integer(4), deserializerContext))
        .thenReturn(value2Renderer);
    when(value2Renderer.render(rendererContext)).thenReturn(4);

    final ConfigurationNode node =
        ConfigurationNode.map(
            Map.of(
                "1", ConfigurationNode.integer(2),
                "3", ConfigurationNode.integer(4)));

    final Map<Integer, Integer> result =
        deserializer.deserialize(node, deserializerContext).render(rendererContext);

    verify(keyDeserializer).deserialize(ConfigurationNode.string("1"), deserializerContext);
    verify(key1Renderer).render(rendererContext);
    verify(valueDeserializer).deserialize(ConfigurationNode.integer(2), deserializerContext);
    verify(value1Renderer).render(rendererContext);
    verify(keyDeserializer).deserialize(ConfigurationNode.string("3"), deserializerContext);
    verify(key2Renderer).render(rendererContext);
    verify(valueDeserializer).deserialize(ConfigurationNode.integer(4), deserializerContext);
    verify(value2Renderer).render(rendererContext);
    assertEquals(Map.of(1, 2, 3, 4), result);
    assertThrows(UnsupportedOperationException.class, () -> result.put(1, 2));
    verifyNoMoreInteractions(key1Renderer, value1Renderer, key2Renderer, value2Renderer);
  }
}
