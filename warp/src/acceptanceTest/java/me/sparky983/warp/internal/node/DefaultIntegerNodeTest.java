package me.sparky983.warp.internal.node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class DefaultIntegerNodeTest {
  @Test
  void testValue() {
    final ConfigurationNode.Integer integer = ConfigurationNode.integer(1);

    assertEquals(1, integer.value());
  }

  @Test
  void testToString() {
    final ConfigurationNode.Integer integer = ConfigurationNode.integer(1);

    assertEquals("1", integer.toString());
  }
}
