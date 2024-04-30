package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class NilNodeTest {
  @Test
  void testToString() {
    assertEquals("nil", ConfigurationNode.nil().toString());
  }
}
