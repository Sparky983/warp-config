package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import me.sparky983.warp.internal.Deserializers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapDeserializerTest {
  Deserializer<Map<String, String>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.map((node) -> "key: " + node, (node) -> "value: " + node);
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
    assertThrows(NullPointerException.class, () -> deserializer.deserialize(null));
  }

  @Test
  void testDeserialize_NonMap() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(DeserializationException.class, () -> deserializer.deserialize(node));
  }

  @Test
  void testDeserialize_Raw() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map()
            .entry("1", ConfigurationNode.integer(2))
            .entry("3", ConfigurationNode.integer(4))
            .build();

    final Map<String, String> result = deserializer.deserialize(node);

    assertEquals(Map.of("key: 1", "value: 2", "key: 3", "value: 4"), result);
  }
}
