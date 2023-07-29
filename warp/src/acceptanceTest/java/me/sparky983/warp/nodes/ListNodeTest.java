package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import me.sparky983.warp.ConfigurationNode;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import org.junit.jupiter.api.Test;

class ListNodeTest {
  @Test
  void testValues() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertEquals(
        List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")),
        list.values());
  }

  @Test
  void testIterator() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertIterableEquals(List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")), list);
  }

  @Test
  void testToString() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertEquals("[1.0, test value]", list.toString());
  }
}
