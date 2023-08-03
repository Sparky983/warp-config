package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptionalDeserializerTest {
  Deserializer<Optional<String>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializer.optional((node) -> "value: " + node);
  }

  @Test
  void testOptional_Null() {
    assertThrows(NullPointerException.class, () -> Deserializer.optional(null));
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> deserializer.deserialize(null));
  }

  @Test
  void testDeserialize_Nil() throws DeserializationException {
    assertEquals(Optional.empty(), deserializer.deserialize(ConfigurationNode.nil()));
  }

  @Test
  void testDeserialize() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(1);

    final Optional<String> result = deserializer.deserialize(node);

    assertEquals(Optional.of("value: 1"), result);
  }
}
