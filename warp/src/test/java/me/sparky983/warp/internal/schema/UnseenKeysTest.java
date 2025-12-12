package me.sparky983.warp.internal.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
class UnseenKeysTest {
  static final ConfigurationError UNKNOWN = ConfigurationError.error("Unknown property");

  @Test
  void testCreateInitialKeys() {
    final Map<String, ConfigurationNode> map =
        Map.ofEntries(
            Map.entry("string", ConfigurationNode.string("value")),
            Map.entry(
                "nested",
                ConfigurationNode.map(
                    Map.entry("string", ConfigurationNode.string("nested value")),
                    Map.entry(
                        "doubly-nested",
                        ConfigurationNode.map(
                            Map.entry(
                                "string", ConfigurationNode.string("doubly nested value")))))));

    final Map<String, UnseenKeys> unseenKeys = UnseenKeys.createInitialKeys(map).unseenKeys();

    assertEquals(2, unseenKeys.size());
    assertNull(unseenKeys.get("string"));
    assertNotNull(unseenKeys.get("nested"));
    assertEquals(2, unseenKeys.get("nested").unseenKeys().size());
    assertNull(unseenKeys.get("nested").unseenKeys().get("string"));
    assertNotNull(unseenKeys.get("nested").unseenKeys().get("doubly-nested"));
    assertEquals(1, unseenKeys.get("nested").unseenKeys().get("doubly-nested").unseenKeys().size());
    assertNull(
        unseenKeys.get("nested").unseenKeys().get("doubly-nested").unseenKeys().get("string"));
  }

  @Test
  void testRemove_NestedValue() {
    final Map<String, ConfigurationNode> map =
        Map.of(
            "nested",
            ConfigurationNode.map(Map.entry("value", ConfigurationNode.string("string"))));

    final UnseenKeys unseenKeys = UnseenKeys.createInitialKeys(map);

    unseenKeys.remove(List.of("nested", "value"));

    assertEquals(1, unseenKeys.unseenKeys().size());
    assertTrue(unseenKeys.unseenKeys().get("nested").unseenKeys().isEmpty());
  }

  @Test
  void testRemove_MapValue() {
    final Map<String, ConfigurationNode> map =
        Map.of(
            "nested",
            ConfigurationNode.map(Map.entry("value", ConfigurationNode.string("string"))));

    final UnseenKeys unseenKeys = UnseenKeys.createInitialKeys(map);

    unseenKeys.remove(List.of("nested"));

    assertTrue(unseenKeys.unseenKeys().isEmpty());
  }

  @Test
  void testMakeErrors() {
    final Map<String, ConfigurationNode> map = new LinkedHashMap<>();
    map.put("string", ConfigurationNode.string("value"));
    map.put(
        "nested",
        ConfigurationNode.map(
            Map.entry("string", ConfigurationNode.string("nested value")),
            Map.entry(
                "doubly-nested",
                ConfigurationNode.map(
                    Map.entry("string", ConfigurationNode.string("doubly nested value"))))));
    final UnseenKeys keys = UnseenKeys.createInitialKeys(map);

    assertEquals(
        List.of(
            ConfigurationError.group("string", UNKNOWN),
            ConfigurationError.group(
                "nested",
                ConfigurationError.group("string", UNKNOWN),
                ConfigurationError.group(
                    "doubly-nested", ConfigurationError.group("string", UNKNOWN)))),
        keys.makeErrors());
  }
}
