package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.Warp;
import me.sparky983.warp.internal.Deserializers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class CustomDeserializerTest {
  @Test
  void testCustomDeserializer_NullType() {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class);

    assertThrows(
        NullPointerException.class, () -> builder.deserializer(null, Deserializers.STRING));
  }

  @Test
  void testCustomDeserializer_NullDeserializer() {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class);

    assertThrows(NullPointerException.class, () -> builder.deserializer(String.class, null));
  }

  @Test
  void testCustomDeserializer_DeserializerThrows() {
    final RuntimeException exception = new RuntimeException();
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()))
            .deserializer(
                String.class,
                (node, context) -> {
                  throw exception;
                });

    final Exception thrown = assertThrows(RuntimeException.class, builder::build);
    assertEquals(exception, thrown);
  }

  @Test
  void testCustomDeserializer_DeserializerThrowsDeserializationException() {
    final Set<ConfigurationError> errors = Set.of(ConfigurationError.error("error 1"), ConfigurationError.error("error 2"));

    final ConfigurationBuilder<Configurations.String> configuration = Warp.builder(Configurations.String.class)
        .source(ConfigurationSource.of(ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()))
        .deserializer(String.class, (node, context) -> {
          throw new DeserializationException(errors);
        });

    final ConfigurationException thrown = assertThrows(ConfigurationException.class, configuration::build);

    assertIterableEquals(List.of(ConfigurationError.group("property", errors)), thrown.errors());
  }

  @Test
  void testCustomDeserializer_DeserializerReturnsNull() {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()))
            .deserializer(String.class, (node, context) -> null);

    assertThrows(NullPointerException.class, builder::build);
  }

  @Test
  void testCustomDeserializer_RendererThrows() throws ConfigurationException {
    final RuntimeException exception = new RuntimeException();
    final Configurations.String configuration =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()))
            .deserializer(
                String.class,
                (node, deserializerContext) ->
                    (rendererContext) -> {
                      throw exception;
                    })
            .build();

    final Exception thrown = assertThrows(RuntimeException.class, configuration::property);
    assertEquals(exception, thrown);
  }

  @Test
  void testCustomDeserializer_RendererReturnsNull() throws ConfigurationException {
    final Configurations.String configuration =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()))
            .deserializer(String.class, (node, deserializerContext) -> (rendererContext) -> null)
            .build();

    assertThrows(NullPointerException.class, configuration::property);
  }

  @Test
  void testCustomDeserializer(
      @Mock final Deserializer<String> deserializer, @Mock final Renderer<String> renderer)
      throws Exception {
    final ConfigurationNode.String node = ConfigurationNode.string("value");

    when(deserializer.deserialize(eq(node), any())).thenReturn(renderer);
    when(renderer.render(any())).thenReturn("value");

    final Configurations.String configuration =
        Warp.builder(Configurations.String.class)
            .source(ConfigurationSource.of(ConfigurationNode.map().entry("property", node).build()))
            .deserializer(String.class, deserializer)
            .build();

    assertEquals("value", configuration.property());
    verify(deserializer).deserialize(eq(node), any());
    verify(renderer).render(any());
    verifyNoMoreInteractions(deserializer, renderer);
  }
}
