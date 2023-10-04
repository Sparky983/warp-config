package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.Deserializers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class OptionalDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  Deserializer<Optional<String>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.optional((node, context) -> Renderer.of("value: " + node));
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testOptional_NullValueDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.optional(null));
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class, () -> deserializer.deserialize(null, deserializerContext));
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.nil();
    final Renderer<Optional<String>> renderer = deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_Nil() throws DeserializationException {
    final Optional<String> result =
        deserializer
            .deserialize(ConfigurationNode.nil(), deserializerContext)
            .render(rendererContext);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void testRender() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.integer(1);

    final Optional<String> result =
        deserializer.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(Optional.of("value: 1"), result);
  }
}
