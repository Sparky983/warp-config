package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class NilNodeTest {
  @Test
  void testNil() {
    final ConfigurationNode.Nil nil = ConfigurationNode.nil();

    assertNotNull(nil);
  }

  @Test
  void testToString() {
    final ConfigurationNode.Nil nil = ConfigurationNode.nil();

    assertEquals("nil", nil.toString());
  }
}
