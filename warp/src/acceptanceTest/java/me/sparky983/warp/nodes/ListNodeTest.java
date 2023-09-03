package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.List;
import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class ListNodeTest {
  @Test
  void testListValues() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")));

    assertEquals(
        List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")),
        list.values());
  }

  @Test
  void testListIterator() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")));

    assertIterableEquals(
        List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")), list);
  }

  @Test
  void testListToString() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")));

    assertEquals("[1.0, test value]", list.toString());
  }

  @Test
  void testVarargsValues() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertEquals(
        List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")),
        list.values());
  }

  @Test
  void testVarargsIterator() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertIterableEquals(
        List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")), list);
  }

  @Test
  void testVarargsToString() {
    final ConfigurationNode.List list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertEquals("[1.0, test value]", list.toString());
  }
}
