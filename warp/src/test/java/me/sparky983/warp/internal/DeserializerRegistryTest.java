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
  void testRegisterDeserializer_NullType() {
    assertThrows(NullPointerException.class, () -> registry.register(null, (node) -> "test"));
  }

  @Test
  void testRegisterDeserializer_NullDeserializer() {
    assertThrows(
        NullPointerException.class,
        () -> registry.register(String.class, (Deserializer<String>) null));
  }

  @Test
  void testRegisterDeserializer() {
    final Deserializer<String> deserializer = (node) -> "test";

    registry.register(String.class, deserializer);

    assertEquals(Optional.of(deserializer), registry.get(ParameterizedType.of(String.class)));
  }

  @Test
  void testRegisterFactory_NullType() {
    assertThrows(
        NullPointerException.class,
        () -> registry.register(null, (registry, type) -> (node) -> "test"));
  }

  @Test
  void testRegisterFactory_NullFactory() {
    assertThrows(
        NullPointerException.class,
        () ->
            registry.register(
                String.class,
                (BiFunction<DeserializerRegistry, ParameterizedType<String>, Deserializer<String>>)
                    null));
  }

  @Test
  void testRegister_AlreadyRegistered() {
    final Deserializer<String> deserializer1 = (node) -> "test";
    final Deserializer<String> deserializer2 = (node) -> "test";

    registry.register(String.class, deserializer1);

    assertThrows(IllegalStateException.class, () -> registry.register(String.class, deserializer2));
    assertThrows(
        IllegalStateException.class,
        () -> registry.register(String.class, (registry, type) -> deserializer2));
    assertEquals(Optional.of(deserializer1), registry.get(ParameterizedType.of(String.class)));
  }

  @Test
  void testGet_NotRegistered() {
    final Optional<Deserializer<String>> deserializer =
        registry.get(ParameterizedType.of(String.class));

    assertEquals(Optional.empty(), deserializer);
  }

  @Test
  void testGet_Factory() {
    final Deserializer<String> deserializer = (node) -> "test";
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
