package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializer.SHORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ShortDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class, () -> SHORT.deserialize(null, ParameterizedTypes.SHORT));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> SHORT.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class, () -> SHORT.deserialize(node, ParameterizedTypes.SHORT));
  }

  @ParameterizedTest
  @ValueSource(ints = {-32769, 32768})
  void testDeserialize_OutOfRange(final int value) {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> SHORT.deserialize(node, ParameterizedTypes.SHORT));

    assertEquals(
        String.format(
            "Must be between %s and %s (both inclusive)", Short.MIN_VALUE, Short.MAX_VALUE),
        thrown.getMessage());
  }

  @ParameterizedTest
  @ValueSource(shorts = {Short.MIN_VALUE, 0, Short.MAX_VALUE})
  void testDeserialize(final int value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final short result = SHORT.deserialize(node, ParameterizedTypes.SHORT);

    assertEquals((short) value, result);
  }
}
