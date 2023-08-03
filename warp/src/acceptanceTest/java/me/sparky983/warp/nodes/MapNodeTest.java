package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MapNodeTest {
  @Nested
  class DefaultBuilderTest {
    @Test
    void testEntry_NullKey() {
      final ConfigurationNode.Map.Builder builder = ConfigurationNode.map();

      final ConfigurationNode value = ConfigurationNode.string("value");

      assertThrows(NullPointerException.class, () -> builder.entry(null, value));
    }

    @Test
    void testEntry_NullValue() {
      final ConfigurationNode.Map.Builder builder = ConfigurationNode.map();

      assertThrows(NullPointerException.class, () -> builder.entry("key", null));
    }

    @Test
    void testEntry() {
      final ConfigurationNode.Map.Builder builder = ConfigurationNode.map();

      assertEquals(builder, builder.entry("key", ConfigurationNode.string("value")));
    }

    @Test
    void testValues() {
      final ConfigurationNode.Map map =
          ConfigurationNode.map()
              .entry("key 1", ConfigurationNode.string("value 1"))
              .entry("key 2", ConfigurationNode.string("value 2"))
              .build();

      assertEquals(
          Map.of(
              "key 1",
              ConfigurationNode.string("value 1"),
              "key 2",
              ConfigurationNode.string("value 2")),
          map.values());
    }

    @Test
    void testGet() {
      final ConfigurationNode.Map map =
          ConfigurationNode.map().entry("key", ConfigurationNode.string("value")).build();

      assertEquals(Optional.of(ConfigurationNode.string("value")), map.get("key"));
    }

    @Test
    void testEntries() {
      final ConfigurationNode.Map map =
          ConfigurationNode.map()
              .entry("key 1", ConfigurationNode.string("value 1"))
              .entry("key 2", ConfigurationNode.string("value 2"))
              .build();

      assertIterableEquals(
          List.of(
              ConfigurationNode.Map.entry("key 1", ConfigurationNode.string("value 1")),
              ConfigurationNode.Map.entry("key 2", ConfigurationNode.string("value 2"))),
          map.entries());
    }

    @Test
    void testToString() {
      final ConfigurationNode.Map map =
          ConfigurationNode.map()
              .entry("key 1", ConfigurationNode.string("value 1"))
              .entry("key 2", ConfigurationNode.string("value 2"))
              .build();

      assertEquals("{key 1=value 1, key 2=value 2}", map.toString());
    }
  }

  @Nested
  class DefaultEntryTest {
    @Test
    void testEntry_NullKey() {
      final ConfigurationNode value = ConfigurationNode.string("value");

      assertThrows(NullPointerException.class, () -> ConfigurationNode.Map.entry(null, value));
    }

    @Test
    void testEntry_NullValue() {
      assertThrows(NullPointerException.class, () -> ConfigurationNode.Map.entry("key", null));
    }

    @Test
    void testKey() {
      final ConfigurationNode.Map.Entry entry =
          ConfigurationNode.Map.entry("key", ConfigurationNode.string("value"));

      assertEquals("key", entry.key());
    }

    @Test
    void testValue() {
      final ConfigurationNode.Map.Entry entry =
          ConfigurationNode.Map.entry("key", ConfigurationNode.string("value"));

      assertEquals(ConfigurationNode.string("value"), entry.value());
    }

    @Test
    void testToString() {
      final ConfigurationNode.Map.Entry entry =
          ConfigurationNode.Map.entry("key", ConfigurationNode.string("value"));

      assertEquals("key=value", entry.toString());
    }
  }
}
