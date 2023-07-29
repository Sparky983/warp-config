package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
class OptionalDeserializerTest {
  @Mock DeserializerRegistry deserializerRegistry;
  Deserializer<Optional> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializer.optional(deserializerRegistry);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerRegistry);
  }

  @Test
  void testOptional_Null() {
    assertThrows(NullPointerException.class, () -> Deserializer.optional(null));
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class,
        () -> deserializer.deserialize(null, ParameterizedTypes.RAW_OPTIONAL));
  }

  @Test
  void testDeserialize_NullType() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonDeserializableValue() {
    when(deserializerRegistry.get(String.class)).thenReturn(Optional.empty());

    final ConfigurationNode node = ConfigurationNode.string("value");

    assertThrows(
        DeserializationException.class,
        () -> deserializer.deserialize(node, ParameterizedTypes.STRING_OPTIONAL));
    verify(deserializerRegistry).get(String.class);
  }

  @Test
  void testDeserialize_Raw() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.string("value");

    final Optional result = deserializer.deserialize(node, ParameterizedTypes.RAW_OPTIONAL);

    assertEquals(Optional.of(ConfigurationNode.string("value")), result);
  }

  @Test
  void testDeserialize_Nil() throws DeserializationException {
    assertEquals(
        Optional.empty(),
        deserializer.deserialize(ConfigurationNode.nil(), ParameterizedTypes.RAW_OPTIONAL));
  }

  @Test
  void testDeserialize() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.string("value");

    when(deserializerRegistry.get(String.class))
        .thenReturn(Optional.of((string, type) -> string + " deserialized"));

    final Optional result = deserializer.deserialize(node, ParameterizedTypes.STRING_OPTIONAL);

    assertEquals(Optional.of("value deserialized"), result);
    verify(deserializerRegistry).get(String.class);
  }
}
