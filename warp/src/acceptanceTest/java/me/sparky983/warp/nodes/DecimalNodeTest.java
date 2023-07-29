package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class DecimalNodeTest {
  @Test
  void testValue() {
    final ConfigurationNode.Decimal decimal = ConfigurationNode.decimal(1.0);

    assertEquals(1.0, decimal.value());
  }

  @Test
  void testToString() {
    final ConfigurationNode.Decimal decimal = ConfigurationNode.decimal(1.0);

    assertEquals("1.0", decimal.toString());
  }
}