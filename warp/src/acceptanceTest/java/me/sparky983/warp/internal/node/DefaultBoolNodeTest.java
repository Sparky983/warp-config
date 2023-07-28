package me.sparky983.warp.internal.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class DefaultBoolNodeTest {
  @Test
  void testValueTrue() {
    final ConfigurationNode.Bool bool = ConfigurationNode.bool(true);

    assertTrue(bool.value());
  }

  @Test
  void testValueFalse() {
    final ConfigurationNode.Bool bool = ConfigurationNode.bool(false);

    assertFalse(bool.value());
  }

  @Test
  void testToString_True() {
    final ConfigurationNode.Bool bool = ConfigurationNode.bool(true);

    assertEquals("true", bool.toString());
  }

  @Test
  void testToString_False() {
    final ConfigurationNode.Bool bool = ConfigurationNode.bool(false);

    assertEquals("false", bool.toString());
  }
}
