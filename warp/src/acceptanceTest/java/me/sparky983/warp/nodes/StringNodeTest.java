package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import org.junit.jupiter.api.Test;

class StringNodeTest {
  @Test
  void testString_Null() {
    assertThrows(NullPointerException.class, () -> ConfigurationNode.string(null));
  }

  @Test
  void testValue() throws DeserializationException {
    final ConfigurationNode string = ConfigurationNode.string("test value");

    assertEquals("test value", string.asString());
  }

  @Test
  void testToString() {
    final ConfigurationNode string = ConfigurationNode.string("test value");

    assertEquals("test value", string.toString());
  }
}
