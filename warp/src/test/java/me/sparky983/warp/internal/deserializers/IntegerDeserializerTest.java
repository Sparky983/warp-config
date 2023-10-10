package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializers.INTEGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import me.sparky983.warp.ConfigurationError;
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
class IntegerDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> INTEGER.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.integer(0);

    assertThrows(NullPointerException.class, () -> INTEGER.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(
        DeserializationException.class, () -> INTEGER.deserialize(node, deserializerContext));
  }

  @ParameterizedTest
  @ValueSource(longs = {-2147483649L, 2147483648L})
  void testDeserialize_OutOfRange(final long value) {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class, () -> INTEGER.deserialize(node, deserializerContext));

    assertIterableEquals(
        List.of(ConfigurationError.error("Must be between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE + " (both inclusive)")),
        thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(0);
    final Renderer<Integer> renderer = INTEGER.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE})
  void testRender(final int value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final int result = INTEGER.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(value, result);
  }
}
