package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import org.junit.jupiter.api.Test;

class ListNodeTest {
  @Test
  void testListValues() throws DeserializationException {
    final ConfigurationNode list =
        ConfigurationNode.list(
            List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")));

    assertEquals(
        List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")),
        list.asList());
  }

  @Test
  void testListToString() {
    final ConfigurationNode list =
        ConfigurationNode.list(
            List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")));

    assertEquals("[1.0, test value]", list.toString());
  }

  @Test
  void testVarargsValues() throws DeserializationException {
    final ConfigurationNode list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertEquals(
        List.of(ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value")),
        list.asList());
  }

  @Test
  void testVarargsToString() {
    final ConfigurationNode list =
        ConfigurationNode.list(
            ConfigurationNode.decimal(1.0), ConfigurationNode.string("test value"));

    assertEquals("[1.0, test value]", list.toString());
  }
}
