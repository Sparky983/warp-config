package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializers.BOOLEAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
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
class BooleanDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> BOOLEAN.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.bool(true);

    assertThrows(NullPointerException.class, () -> BOOLEAN.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonBoolean() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final ConfigurationException thrown = assertThrows(
        DeserializationException.class, () -> BOOLEAN.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a boolean")), thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.bool(true);
    final Renderer<Boolean> renderer = BOOLEAN.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testRender(final boolean value) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.bool(value);

    final boolean result = BOOLEAN.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(value, result);
  }
}
