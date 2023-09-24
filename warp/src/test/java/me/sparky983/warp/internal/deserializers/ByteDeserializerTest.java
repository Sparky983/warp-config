package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializers.BYTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class ByteDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> BYTE.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.integer(0);

    assertThrows(NullPointerException.class, () -> BYTE.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(DeserializationException.class, () -> BYTE.deserialize(node, deserializerContext));
  }

  @ParameterizedTest
  @ValueSource(ints = {-129, 128})
  void testDeserialize_OutOfRange(final int value) {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, () -> BYTE.deserialize(node, deserializerContext));

    assertEquals(
        "Must be between " + Byte.MIN_VALUE + " and " + Byte.MAX_VALUE + " (both inclusive)",
        thrown.getMessage());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(0);
    final Renderer<Byte> renderer = BYTE.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @ParameterizedTest
  @ValueSource(bytes = {-128, 0, 127})
  void testRender(final byte value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final byte result = BYTE.deserialize(node, deserializerContext)
        .render(rendererContext);

    assertEquals(value, result);
  }
}
