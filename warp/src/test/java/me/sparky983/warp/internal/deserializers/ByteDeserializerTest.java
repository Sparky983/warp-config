package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializer.BYTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ByteDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> BYTE.deserialize(null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(DeserializationException.class, () -> BYTE.deserialize(node));
  }

  @ParameterizedTest
  @ValueSource(ints = {-129, 128})
  void testDeserialize_OutOfRange(final int value) {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, () -> BYTE.deserialize(node));

    assertEquals(
            "Must be between " + Byte.MIN_VALUE + " and " + Byte.MAX_VALUE + " (both inclusive)",
        thrown.getMessage());
  }

  @ParameterizedTest
  @ValueSource(bytes = {-128, 0, 127})
  void testDeserialize(final byte value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final byte result = BYTE.deserialize(node);

    assertEquals(value, result);
  }
}
