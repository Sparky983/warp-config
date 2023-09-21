package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import me.sparky983.warp.internal.Deserializers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListDeserializerTest {
  Deserializer<List<String>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.list((node) -> "element: " + node);
  }

  @Test
  void testList_Null() {
    assertThrows(NullPointerException.class, () -> Deserializers.list(null));
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> deserializer.deserialize(null));
  }

  @Test
  void testDeserialize_NonList() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(DeserializationException.class, () -> deserializer.deserialize(node));
  }

  @Test
  void testDeserialize() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.list(ConfigurationNode.integer(1), ConfigurationNode.integer(2));

    final List<String> result = deserializer.deserialize(node);

    assertEquals(List.of("element: 1", "element: 2"), result);
  }
}
