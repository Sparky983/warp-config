package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import me.sparky983.warp.internal.DeserializerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@SuppressWarnings("rawtypes")
@MockitoSettings
class ListDeserializerTest {
  @Mock DeserializerRegistry deserializerRegistry;
  Deserializer<List> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializer.list(deserializerRegistry);
  }

  @Test
  void testList_Null() {
    assertThrows(NullPointerException.class, () -> Deserializer.list(null));
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class,
        () -> deserializer.deserialize(null, ParameterizedTypes.RAW_LIST));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonList() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class,
        () -> deserializer.deserialize(node, ParameterizedTypes.RAW_LIST));
  }

  @Test
  void testDeserialize_NonDeserializableElement() {
    when(deserializerRegistry.get(String.class)).thenReturn(Optional.empty());

    final ConfigurationNode node = ConfigurationNode.list();

    assertThrows(
        DeserializationException.class,
        () -> deserializer.deserialize(node, ParameterizedTypes.STRING_LIST));
  }

  @Test
  void testDeserialize_Raw() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.list(
            ConfigurationNode.string("element 1"), ConfigurationNode.string("element 2"));

    final List result = deserializer.deserialize(node, ParameterizedTypes.RAW_LIST);

    assertEquals(
        List.of(ConfigurationNode.string("element 1"), ConfigurationNode.string("element 2")),
        result);
  }

  @Test
  void testDeserialize() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.list(
            ConfigurationNode.string("element 1"), ConfigurationNode.string("element 2"));

    when(deserializerRegistry.get(String.class)).thenReturn(Optional.of((string, type) -> string + " deserialized"));

    final List result = deserializer.deserialize(node, ParameterizedTypes.STRING_LIST);

    assertEquals(List.of("element 1 deserialized", "element 2 deserialized"), result);
  }
}
