package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializer.INTEGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IntegerDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class, () -> INTEGER.deserialize(null, ParameterizedTypes.INTEGER));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> INTEGER.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class,
        () -> INTEGER.deserialize(node, ParameterizedTypes.INTEGER));
  }

  @ParameterizedTest
  @ValueSource(longs = {-2147483649L, 2147483648L})
  void testDeserialize_OutOfRange(final long value) {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> INTEGER.deserialize(node, ParameterizedTypes.INTEGER));

    assertEquals(
        String.format(
            "Must be between %s and %s (both inclusive)", Integer.MIN_VALUE, Integer.MAX_VALUE),
        thrown.getMessage());
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE})
  void testDeserialize(final int value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final int result = INTEGER.deserialize(node, ParameterizedTypes.INTEGER);

    assertEquals(value, result);
  }
}
