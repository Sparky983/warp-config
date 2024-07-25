package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import org.junit.jupiter.api.Test;

class IntegerNodeTest {
  @Test
  void testAsInteger() throws DeserializationException {
    final ConfigurationNode integer = ConfigurationNode.integer(1);

    assertEquals(1, integer.asInteger());
  }

  @Test
  void testToString() {
    final ConfigurationNode integer = ConfigurationNode.integer(1);

    assertEquals("1", integer.toString());
  }
}
