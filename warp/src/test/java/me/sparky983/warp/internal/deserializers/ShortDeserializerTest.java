package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializers.SHORT;
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
class ShortDeserializerTest {
  @Mock Deserializer.Context deserializeContext;
  @Mock Renderer.Context renderContext;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializeContext, renderContext);
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> SHORT.deserialize(null, deserializeContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.integer(0);

    assertThrows(NullPointerException.class, () -> SHORT.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(DeserializationException.class, () -> SHORT.deserialize(node, deserializeContext));
  }

  @ParameterizedTest
  @ValueSource(ints = {-32769, 32768})
  void testDeserialize_OutOfRange(final int value) {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, () -> SHORT.deserialize(node, deserializeContext));

    assertEquals(
        "Must be between " + Short.MIN_VALUE + " and " + Short.MAX_VALUE + " (both inclusive)",
        thrown.getMessage());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(0);
    final Renderer<Short> renderer = SHORT.deserialize(node, deserializeContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @ParameterizedTest
  @ValueSource(shorts = {Short.MIN_VALUE, 0, Short.MAX_VALUE})
  void testRender(final int value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final short result = SHORT.deserialize(node, deserializeContext)
        .render(renderContext);

    assertEquals((short) value, result);
  }
}
