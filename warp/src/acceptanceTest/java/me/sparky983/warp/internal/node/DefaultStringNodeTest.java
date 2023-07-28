package me.sparky983.warp.internal.node;

import me.sparky983.warp.ConfigurationNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class DefaultStringNodeTest {
  @Test
  void testString_Null() {
    assertThrows(NullPointerException.class, () -> ConfigurationNode.string(null));
  }

  @Test
  void testValue() {
    final ConfigurationNode.String string = ConfigurationNode.string("test value");

    assertEquals("test value", string.value());
  }

  @Test
  void testToString() {
    final ConfigurationNode.String string = ConfigurationNode.string("test value");

    assertEquals("test value", string.toString());
  }
}
