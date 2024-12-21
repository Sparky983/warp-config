package me.sparky983.warp.internal.deserializers;

import static me.sparky983.warp.internal.deserializers.Deserializers.STRING;
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
class StringDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.string("value");

    assertThrows(NullPointerException.class, () -> STRING.deserialize(node, null));
  }

  @Test
  void testDeserializer_NullNode() {
    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class, () -> STRING.deserialize(null, deserializerContext));

    assertIterableEquals(
        List.of(ConfigurationError.error("Must be set to a value")), thrown.errors());
  }

  @Test
  void testDeserialize_NonString() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class, () -> STRING.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a string")), thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.string("value");
    final Renderer<String> renderer = STRING.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.string("value");

    final String result = STRING.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals("value", result);
  }
}
