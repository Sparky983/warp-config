package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import org.junit.jupiter.api.Test;

class MapNodeTest {
  Map.Entry<String, ConfigurationNode> entry(final String key, final ConfigurationNode value) {
    return Collections.singletonMap(key, value).entrySet().iterator().next();
  }

  @Test
  void testEntries_NullKey() {
    final Map.Entry<String, ConfigurationNode> entry =
        entry(null, ConfigurationNode.string("value"));

    assertThrows(NullPointerException.class, () -> ConfigurationNode.map(entry));
  }

  @Test
  void testEntries_NullValue() {
    final Map.Entry<String, ConfigurationNode> entry = entry("key", null);

    assertThrows(NullPointerException.class, () -> ConfigurationNode.map(entry));
  }

  @Test
  void testEntries_asMap() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map(
            Map.entry("key 1", ConfigurationNode.string("value 1")),
            Map.entry("key 2", ConfigurationNode.string("value 2")));

    assertEquals(
        Map.of(
            "key 1",
            ConfigurationNode.string("value 1"),
            "key 2",
            ConfigurationNode.string("value 2")),
        node.asMap());
  }

  @Test
  void testToString() {
    final ConfigurationNode map =
        ConfigurationNode.map(
            Map.entry("key 1", ConfigurationNode.string("value 1")),
            Map.entry("key 2", ConfigurationNode.string("value 2")));

    assertEquals("{key 1=value 1, key 2=value 2}", map.toString());
  }
}
