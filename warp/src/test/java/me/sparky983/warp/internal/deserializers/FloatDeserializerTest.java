package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.Deserializers.FLOAT;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class FloatDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(NullPointerException.class, () -> FLOAT.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.decimal(0.0);

    assertThrows(NullPointerException.class, () -> FLOAT.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonNumber() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class, () -> FLOAT.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a number")), thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.decimal(0.0);
    final Renderer<Float> renderer = FLOAT.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_Integer() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(1);

    final float result = FLOAT.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(1.0, result);
  }

  @Test
  void testRender_Decimal() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.decimal(1.5);

    final float result = FLOAT.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(1.5, result);
  }
}
