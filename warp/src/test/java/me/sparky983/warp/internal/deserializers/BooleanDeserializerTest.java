package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializer.BOOLEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BooleanDeserializerTest {
  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class, () -> BOOLEAN.deserialize(null, ParameterizedTypes.BOOLEAN));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> BOOLEAN.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonBoolean() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class,
        () -> BOOLEAN.deserialize(node, ParameterizedTypes.BOOLEAN));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testDeserialize(final boolean value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.bool(value);

    final boolean result = BOOLEAN.deserialize(node, ParameterizedTypes.BOOLEAN);

    assertEquals(value, result);
  }
}
