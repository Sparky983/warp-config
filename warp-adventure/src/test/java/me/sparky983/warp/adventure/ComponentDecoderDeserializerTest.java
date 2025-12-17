package me.sparky983.warp.adventure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class ComponentDecoderDeserializerTest {
  @Mock ComponentDecoder<String, Component> decoder;
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;
  Deserializer<Component> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = ComponentDeserializer.deserializer(decoder);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(decoder, deserializerContext, rendererContext);
  }

  @Test
  void testDeserializer_NullDecoder() {
    assertThrows(NullPointerException.class, () -> ComponentDeserializer.deserializer(null));
  }

  @Test
  void testDeserialize_NullNode() {
    final ConfigurationException thrown = assertThrows(
        ConfigurationException.class, () -> deserializer.deserialize(null, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be set to a value")), thrown.errors());
  }

  @Test
  void testDeserialize_NullContext() {
    assertThrows(
        NullPointerException.class,
        () -> deserializer.deserialize(ConfigurationNode.string("abc"), null));
  }

  @Test
  void testDeserialize_NonString() {
    final ConfigurationNode node = ConfigurationNode.nil();

    final ConfigurationException thrown = assertThrows(ConfigurationException.class, () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a string")), thrown.errors());
  }

  @Test
  void testRender() throws DeserializationException {
    when(decoder.deserialize("Hello\\nworld")).thenReturn(Component.text("Hello\nworld"));

    final Component component = deserializer.deserialize(ConfigurationNode.string("Hello\\nworld"), deserializerContext)
        .render(rendererContext);

    verify(decoder).deserialize("Hello\\nworld");
    assertEquals(Component.text("Hello\nworld"), component);
  }
}
