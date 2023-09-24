package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Map;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.Deserializers;
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
    deserializer = Deserializers.map((node, context) -> Renderer.of("key: " + node), (node, context) -> Renderer.of("value: " + node));
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
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> deserializer.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.map().build();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonMap() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(DeserializationException.class, () -> deserializer.deserialize(node, deserializerContext));
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map()
            .entry("1", ConfigurationNode.integer(2))
            .entry("3", ConfigurationNode.integer(4))
            .build();

    final Renderer<Map<String, String>> renderer = deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map()
            .entry("1", ConfigurationNode.integer(2))
            .entry("3", ConfigurationNode.integer(4))
            .build();

    final Map<String, String> result = deserializer.deserialize(node, deserializerContext)
        .render(rendererContext);

    assertEquals(Map.of("key: 1", "value: 2", "key: 3", "value: 4"), result);
    assertThrows(UnsupportedOperationException.class, () -> result.put("key: 1", "value: 2"));
  }
}
