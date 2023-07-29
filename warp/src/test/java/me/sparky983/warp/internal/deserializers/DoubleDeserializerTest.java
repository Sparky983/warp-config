package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializer.DOUBLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;

class DoubleDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class, () -> DOUBLE.deserialize(null, ParameterizedTypes.DOUBLE));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> DOUBLE.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonNumber() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class, () -> DOUBLE.deserialize(node, ParameterizedTypes.DOUBLE));
  }

  @Test
  void testDeserialize_Integer() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(1);

    final double result = DOUBLE.deserialize(node, ParameterizedTypes.DOUBLE);

    assertEquals(1.0, result);
  }

  @Test
  void testDeserialize_Decimal() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.decimal(1.5);

    final double result = DOUBLE.deserialize(node, ParameterizedTypes.DOUBLE);

    assertEquals(1.5, result);
  }
}
