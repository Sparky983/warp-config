package me.sparky983.warp.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeserializerRegistryTest {
  DeserializerRegistry registry;

  @BeforeEach
  void setUp() {
    registry = DeserializerRegistry.create();
  }

  @Test
  void testRegistry_NullType() {
    assertThrows(NullPointerException.class, () -> registry.register(null, (node) -> "test"));
  }

  @Test
  void testRegister_NullDeserializer() {
    assertThrows(
        NullPointerException.class,
        () -> registry.register(String.class, (Deserializer<String>) null));
  }

  @Test
  void testRegistryFactory_NullType() {
    assertThrows(
        NullPointerException.class,
        () -> registry.register(null, (registry, type) -> (node) -> "test"));
  }

  @Test
  void testRegistryFactory_NullFactory() {
    assertThrows(
        NullPointerException.class,
        () ->
            registry.register(
                String.class,
                (BiFunction<DeserializerRegistry, ParameterizedType<String>, Deserializer<String>>)
                    null));
  }

  @Test
  void testGet_NotRegistered() {
    final Optional<Deserializer<String>> deserializer =
        registry.get(ParameterizedType.of(String.class));

    assertEquals(Optional.empty(), deserializer);
  }

  @Test
  void testGet_Registered() {
    final Deserializer<String> overwritten = (node) -> "test";
    final Deserializer<String> deserializer = (node) -> "test";
    registry.register(String.class, overwritten);
    registry.register(String.class, deserializer);

    final Optional<Deserializer<String>> result = registry.get(ParameterizedType.of(String.class));

    assertEquals(Optional.of(deserializer), result);
  }

  @Test
  void testGet_Factory() {
    final Deserializer<String> overwritten = (node) -> "test";
    final Deserializer<String> deserializer = (node) -> "test";
    registry.register(String.class, overwritten);
    registry.register(
        String.class,
        (registry, type) -> {
          assertEquals(this.registry, registry);
          assertEquals(ParameterizedType.of(String.class), type);
          return deserializer;
        });

    final Optional<Deserializer<String>> result = registry.get(ParameterizedType.of(String.class));

    assertEquals(Optional.of(deserializer), result);
  }

  @Test
  void testGet_FactoryThrowsIllegalStateException() {
    final IllegalStateException exception = new IllegalStateException();

    registry.register(
        String.class,
        (registry, type) -> {
          throw exception;
        });

    final IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class, () -> registry.get(ParameterizedType.of(String.class)));

    assertEquals(exception, thrown);
  }

  @Test
  void testGet_FactoryThrows() {
    final RuntimeException exception = new RuntimeException();

    registry.register(
        String.class,
        (registry, type) -> {
          throw exception;
        });

    final IllegalStateException thrown =
        assertThrows(
            IllegalStateException.class, () -> registry.get(ParameterizedType.of(String.class)));

    assertEquals(exception, thrown.getCause());
  }

  @Test
  void testGet_FactoryReturnsNull() {
    registry.register(String.class, (registry, type) -> null);

    assertThrows(
        IllegalStateException.class, () -> registry.get(ParameterizedType.of(String.class)));
  }
}
