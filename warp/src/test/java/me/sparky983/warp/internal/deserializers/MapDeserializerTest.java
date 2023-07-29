package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ParameterizedTypes;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import me.sparky983.warp.internal.DeserializerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@SuppressWarnings("rawtypes")
@MockitoSettings
class MapDeserializerTest {
  @Mock DeserializerRegistry deserializerRegistry;
  Deserializer<Map> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializer.map(deserializerRegistry);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerRegistry);
  }

  @Test
  void testMap_Null() {
    assertThrows(NullPointerException.class, () -> Deserializer.map(null));
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class,
        () -> deserializer.deserialize(null, ParameterizedTypes.RAW_MAP));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonMap() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class,
        () -> deserializer.deserialize(node, ParameterizedTypes.RAW_MAP));
  }

  @Test
  void testDeserialize_NonDeserializableKey() {
    when(deserializerRegistry.get(Integer.class)).thenReturn(Optional.empty());

    final ConfigurationNode node = ConfigurationNode.map().build();

    assertThrows(
        DeserializationException.class,
        () -> deserializer.deserialize(node, ParameterizedTypes.INTEGER_STRING_MAP));
    verify(deserializerRegistry).get(Integer.class);
  }

  @Test
  void testDeserialize_NonDeserializableValue() {
    when(deserializerRegistry.get(Integer.class)).thenReturn(Optional.of((node, type) -> 1));
    when(deserializerRegistry.get(String.class)).thenReturn(Optional.empty());

    final ConfigurationNode node = ConfigurationNode.map().build();

    assertThrows(
        DeserializationException.class,
        () -> deserializer.deserialize(node, ParameterizedTypes.INTEGER_STRING_MAP));
    verify(deserializerRegistry).get(Integer.class);
  }

  @Test
  void testDeserialize_Raw() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map()
            .entry("1", ConfigurationNode.string("value 1"))
            .entry("2", ConfigurationNode.string("value 2"))
            .build();

    final Map result = deserializer.deserialize(node, ParameterizedTypes.RAW_MAP);

    assertEquals(
        Map.of("1", ConfigurationNode.string("value 1"), "2", ConfigurationNode.string("value 2")),
        result);
  }

  @Test
  void testDeserialize() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.map()
            .entry("1", ConfigurationNode.string("value 1"))
            .entry("2", ConfigurationNode.string("value 2"))
            .build();

    when(deserializerRegistry.get(Integer.class))
        .thenReturn(Optional.of((integer, type) -> Integer.parseInt(integer.toString())));
    when(deserializerRegistry.get(String.class))
        .thenReturn(Optional.of((string, type) -> string + " deserialized"));

    final Map result = deserializer.deserialize(node, ParameterizedTypes.INTEGER_STRING_MAP);

    assertEquals(Map.of(1, "value 1 deserialized", 2, "value 2 deserialized"), result);
    verify(deserializerRegistry).get(Integer.class);
  }
}
