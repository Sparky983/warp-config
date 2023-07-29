package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializer.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;

class StringDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class, () -> STRING.deserialize(null, ParameterizedTypes.STRING));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> STRING.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonString() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class, () -> STRING.deserialize(node, ParameterizedTypes.STRING));
  }

  @Test
  void testDeserialize() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.string("value");

    final String result = STRING.deserialize(node, ParameterizedTypes.STRING);

    assertEquals("value", result);
  }
}