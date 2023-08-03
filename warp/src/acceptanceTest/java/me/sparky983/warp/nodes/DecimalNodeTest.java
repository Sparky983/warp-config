package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class DecimalNodeTest {
  @Test
  void testDecimal_NaN() {
    assertThrows(IllegalArgumentException.class, () -> ConfigurationNode.decimal(Double.NaN));
  }

  @Test
  void testDecimal_PositiveInfinity() {
    assertThrows(
        IllegalArgumentException.class, () -> ConfigurationNode.decimal(Double.POSITIVE_INFINITY));
  }

  @Test
  void testDecimal_NegativeInfinity() {
    assertThrows(
        IllegalArgumentException.class, () -> ConfigurationNode.decimal(Double.NEGATIVE_INFINITY));
  }

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
