package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import me.sparky983.warp.ConfigurationError;
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
class ListDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;
  @Mock Deserializer<Integer> elementDeserializer;
  Deserializer<List<Integer>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.list(elementDeserializer);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext, elementDeserializer);
  }

  @Test
  void testList_NullElementDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.list(null));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.list();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonList() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a list")), thrown.errors());
  }

  @Test
  void testDeserialize_NestedNonDeserializable(@Mock final Renderer<Integer> validRenderer)
      throws DeserializationException {
    when(elementDeserializer.deserialize(ConfigurationNode.nil(), deserializerContext))
        .thenThrow(new DeserializationException(ConfigurationError.error("Must be an integer")));
    when(elementDeserializer.deserialize(ConfigurationNode.integer(1), deserializerContext))
        .thenReturn(validRenderer);

    final ConfigurationNode node =
        ConfigurationNode.list(
            ConfigurationNode.nil(), ConfigurationNode.integer(1), ConfigurationNode.nil());

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(
        List.of(
            ConfigurationError.group("0", ConfigurationError.error("Must be an integer")),
            ConfigurationError.group("2", ConfigurationError.error("Must be an integer"))),
        thrown.errors());
    verifyNoMoreInteractions(validRenderer);
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.list();
    final Renderer<List<Integer>> renderer = deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_NullNode() throws DeserializationException {
    final List<Integer> result =
        deserializer.deserialize(null, deserializerContext).render(rendererContext);

    assertEquals(List.of(), result);
  }

  @Test
  void testRender(
      @Mock final Renderer<Integer> element1Renderer,
      @Mock final Renderer<Integer> element2Renderer)
      throws DeserializationException {
    when(elementDeserializer.deserialize(ConfigurationNode.integer(1), deserializerContext))
        .thenReturn(element1Renderer);
    when(element1Renderer.render(rendererContext)).thenReturn(1);
    when(elementDeserializer.deserialize(ConfigurationNode.integer(2), deserializerContext))
        .thenReturn(element2Renderer);
    when(element2Renderer.render(rendererContext)).thenReturn(2);

    final ConfigurationNode node =
        ConfigurationNode.list(ConfigurationNode.integer(1), ConfigurationNode.integer(2));

    final List<Integer> result =
        deserializer.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(List.of(1, 2), result);
    assertThrows(UnsupportedOperationException.class, () -> result.add(3));
    verify(element1Renderer).render(rendererContext);
    verify(element2Renderer).render(rendererContext);
    verifyNoMoreInteractions(element1Renderer, element2Renderer);
  }
}
