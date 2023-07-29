package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class NilNodeTest {
  @Test
  void testEquals_DifferentType() {
    assertNotEquals(ConfigurationNode.nil(), new Object());
  }

  @Test
  void testEquals_Same() {
    assertEquals(ConfigurationNode.nil(), ConfigurationNode.nil());
  }

  @Test
  void testEquals_SameType() {
    assertEquals(ConfigurationNode.nil(), new ConfigurationNode.Nil() {});
  }

  @Test
  void testHashCode() {
    assertEquals(0, ConfigurationNode.nil().hashCode());
  }

  @Test
  void testToString() {
    assertEquals("nil", ConfigurationNode.nil().toString());
  }
}
