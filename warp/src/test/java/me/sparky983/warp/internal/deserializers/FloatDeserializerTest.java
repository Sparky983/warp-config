package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializers.FLOAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;

class FloatDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> FLOAT.deserialize(null));
  }

  @Test
  void testDeserialize_NonNumber() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(DeserializationException.class, () -> FLOAT.deserialize(node));
  }

  @Test
  void testDeserialize_Integer() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(1);

    final float result = FLOAT.deserialize(node);

    assertEquals(1.0, result);
  }

  @Test
  void testDeserialize_Decimal() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.decimal(1.5);

    final float result = FLOAT.deserialize(node);

    assertEquals(1.5, result);
  }
}
