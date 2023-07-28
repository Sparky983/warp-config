package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializer.LONG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LongDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> LONG.deserialize(null, ParameterizedTypes.LONG));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> LONG.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class, () -> LONG.deserialize(node, ParameterizedTypes.LONG));
  }

  @ParameterizedTest
  @ValueSource(longs = {Long.MIN_VALUE, 0, Long.MAX_VALUE})
  void testDeserialize(final long value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final long result = LONG.deserialize(node, ParameterizedTypes.LONG);

    assertEquals(value, result);
  }
}
