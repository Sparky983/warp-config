package me.sparky983.warp.internal.deserializers;

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
import me.sparky983.warp.internal.Deserializers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class ListDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  Deserializer<List<String>> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.list((node, context) -> Renderer.of("element: " + node));
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testList_NullElementDeserializer() {
    assertThrows(NullPointerException.class, () -> Deserializers.list(null));
  }

  @Test
  void testDeserialize_NullNode() {
    assertThrows(
        NullPointerException.class, () -> deserializer.deserialize(null, deserializerContext));
  }

  @Test
  void testDeserialize_NullContext() {
    final ConfigurationNode node = ConfigurationNode.list();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonList() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown = assertThrows(
        DeserializationException.class, () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a list")), thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.list();
    final Renderer<List<String>> renderer = deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender() throws DeserializationException {
    final ConfigurationNode node =
        ConfigurationNode.list(ConfigurationNode.integer(1), ConfigurationNode.integer(2));

    final List<String> result =
        deserializer.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(List.of("element: 1", "element: 2"), result);
    assertThrows(UnsupportedOperationException.class, () -> result.add("element: 3"));
  }
}
