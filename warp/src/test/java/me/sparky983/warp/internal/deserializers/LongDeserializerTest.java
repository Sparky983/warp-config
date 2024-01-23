package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.deserializers.Deserializers.LONG;
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
class LongDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> LONG.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.integer(0);

    assertThrows(NullPointerException.class, () -> LONG.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class, () -> LONG.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be an integer")), thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(0);
    final Renderer<Long> renderer = LONG.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @ParameterizedTest
  @ValueSource(longs = {Long.MIN_VALUE, 0, Long.MAX_VALUE})
  void testRender(final long value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(value);

    final long result = LONG.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(value, result);
  }
}
