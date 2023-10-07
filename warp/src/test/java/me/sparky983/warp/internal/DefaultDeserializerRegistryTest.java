package me.sparky983.warp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import org.junit.jupiter.api.Test;

class DefaultDeserializerRegistryTest {
  @Test
  void testBuilderDeserializer_NullType() {
    final DeserializerRegistry.Builder builder = DeserializerRegistry.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.deserializer(null, (node, context) -> Renderer.of("test")));
  }

  @Test
  void testBuilderDeserializer_NullDeserializer() {
    final DeserializerRegistry.Builder builder = DeserializerRegistry.builder();

    assertThrows(NullPointerException.class, () -> builder.deserializer(String.class, null));
  }

  @Test
  void testBuilderDeserializer() {
    final DeserializerRegistry.Builder builder = DeserializerRegistry.builder();
    final Deserializer<String> deserializer = (node, context) -> Renderer.of("test");

    assertEquals(builder, builder.deserializer(String.class, deserializer));

    final DeserializerRegistry registry = builder.build();
    assertEquals(Optional.of(deserializer), registry.get(ParameterizedType.of(String.class)));
  }

  @Test
  void testBuilderFactory_NullType() {
    final DeserializerRegistry.Builder builder = DeserializerRegistry.builder();

    assertThrows(
        NullPointerException.class,
        () -> builder.factory(null, (registry, type) -> (node, context) -> Renderer.of("test")));
  }

  @Test
  void testBuilderFactory_NullFactory() {
    final DeserializerRegistry.Builder builder = DeserializerRegistry.builder();

    assertThrows(NullPointerException.class, () -> builder.factory(String.class, null));
  }

  @Test
  void testBuilderFactory_AlreadyRegistered() {
    final DeserializerRegistry.Builder builder = DeserializerRegistry.builder();

    final Deserializer<String> deserializer1 = (node, context) -> Renderer.of("test");
    final Deserializer<String> deserializer2 = (node, context) -> Renderer.of("test");

    builder.deserializer(String.class, deserializer1);
    builder.deserializer(String.class, deserializer2);

    final DeserializerRegistry registry = builder.build();

    assertEquals(Optional.of(deserializer2), registry.get(ParameterizedType.of(String.class)));
  }

  @Test
  void testGet_NotRegistered() {
    final DeserializerRegistry registry = DeserializerRegistry.builder().build();

    final Optional<Deserializer<String>> deserializer =
        registry.get(ParameterizedType.of(String.class));

    assertEquals(Optional.empty(), deserializer);
  }

  @Test
  void testBuilderFactory() {
    final DeserializerRegistry.Builder builder = DeserializerRegistry.builder();

    final Deserializer<String> deserializer = (node, context) -> Renderer.of("test");
    final AtomicReference<DeserializerRegistry> registryRef = new AtomicReference<>();
    builder.factory(
        String.class,
        (registry, type) -> {
          assertEquals(registryRef.get(), registry);
          assertEquals(ParameterizedType.of(String.class), type);
          return deserializer;
        });

    final DeserializerRegistry registry = builder.build();
    registryRef.set(registry);

    final Optional<Deserializer<String>> result = registry.get(ParameterizedType.of(String.class));

    assertEquals(Optional.of(deserializer), result);
  }

  @Test
  void testGet_FactoryThrowsIllegalStateException() {
    final IllegalStateException exception = new IllegalStateException();
    final DeserializerRegistry registry =
        DeserializerRegistry.builder()
            .factory(
                String.class,
                (deserializerRegistry, type) -> {
                  throw exception;
                })
            .build();

    final IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class, () -> registry.get(ParameterizedType.of(String.class)));

    assertEquals(exception, thrown);
  }

  @Test
  void testGet_FactoryThrows() {
    final RuntimeException exception = new RuntimeException();
    final DeserializerRegistry registry =
        DeserializerRegistry.builder()
            .factory(
                String.class,
                (deserializerRegistry, type) -> {
                  throw exception;
                })
            .build();

    final IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class, () -> registry.get(ParameterizedType.of(String.class)));

    assertEquals(exception, thrown.getCause());
  }

  @Test
  void testGet_FactoryReturnsNull() {
    final DeserializerRegistry registry =
        DeserializerRegistry.builder()
            .factory(String.class, (deserializerRegistry, type) -> null)
            .build();

    assertThrows(
        IllegalStateException.class, () -> registry.get(ParameterizedType.of(String.class)));
  }
}
