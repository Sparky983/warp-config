package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.annotation.RetentionPolicy;
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
class EnumDeserializerTest {
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;

  Deserializer<RetentionPolicy> deserializer;

  @BeforeEach
  void setUp() {
    deserializer = Deserializers.enumeration(RetentionPolicy.class);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(deserializerContext, rendererContext);
  }

  @Test
  void testEnumeration_NullType() {
    assertThrows(NullPointerException.class, () -> Deserializers.enumeration(null));
  }

  @Test
  void testDeserialize_NullRenderer() {
    final ConfigurationNode node = ConfigurationNode.string("RUNTIME");

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NullNode() {
    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(null, deserializerContext));

    assertIterableEquals(
        List.of(ConfigurationError.error("Must be set to a value")), thrown.errors());
  }

  @Test
  void testDeserialize_IllegalValue() {
    final ConfigurationNode node = ConfigurationNode.string("ILLEGAL_VALUE");

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(
        List.of(ConfigurationError.error("ILLEGAL_VALUE is not a valid value")), thrown.errors());
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.string("RUNTIME");
    final Renderer<RetentionPolicy> renderer = deserializer.deserialize(node, deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender() throws DeserializationException {
    final ConfigurationNode node = ConfigurationNode.string("RUNTIME");

    final RetentionPolicy result =
        deserializer.deserialize(node, deserializerContext).render(rendererContext);

    assertEquals(RetentionPolicy.RUNTIME, result);
  }
}
