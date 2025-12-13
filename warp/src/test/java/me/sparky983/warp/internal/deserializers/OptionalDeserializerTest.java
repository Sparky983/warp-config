package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class OptionalDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;
  @Mock Deserializer<Integer> valueDeserializer;
  Deserializer<Optional<Integer>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.optional(valueDeserializer);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext, valueDeserializer);
  }

  @Test
  void testOptional_NullValueDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.optional(null));
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.nil();
    final Renderer<Optional<Integer>> renderer =
        deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_Nil() throws DeserializationException {
    final Optional<Integer> result =
        deserializer
            .deserialize(ConfigurationNode.nil(), deserializerContext)
            .render(rendererContext);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void testRender_NullNode() throws DeserializationException {
    final Optional<Integer> result =
        deserializer.deserialize(null, deserializerContext).render(rendererContext);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void testRender(@Mock final Renderer<Integer> valueRenderer) throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(1);
    when(valueDeserializer.deserialize(node, deserializerContext)).thenReturn(valueRenderer);
    when(valueRenderer.render(rendererContext)).thenReturn(1);

    final Optional<Integer> result =
        deserializer.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(Optional.of(1), result);
  }
}
