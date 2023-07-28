package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class DefaultNilNodeTest {
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
