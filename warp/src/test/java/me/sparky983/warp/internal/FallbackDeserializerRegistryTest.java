package me.sparky983.warp.internal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.sparky983.warp.Deserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class FallbackDeserializerRegistryTest {
  @Mock DeserializerRegistry registry;
  @Mock DeserializerRegistry fallback;

  DeserializerRegistry fallbackRegistry;

  @BeforeEach
  void setUp() {
    fallbackRegistry = new FallbackDeserializerRegistry(registry, fallback);
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(registry, fallback);
  }

  @Test
  void testNew_NullRegistry() {
    assertThrows(
        NullPointerException.class, () -> new FallbackDeserializerRegistry(null, fallback));
  }

  @Test
  void testNew_NullFallback() {
    assertThrows(
        NullPointerException.class, () -> new FallbackDeserializerRegistry(registry, null));
  }

  @Test
  void testGet_Null() {
    assertThrows(NullPointerException.class, () -> fallbackRegistry.get(null));
  }

  @Test
  void testGet_Registry(@Mock final Deserializer<String> deserializer) {
    final ParameterizedType<String> type = ParameterizedType.of(String.class);

    when(registry.get(type)).thenReturn(Optional.of(deserializer));

    assertEquals(Optional.of(deserializer), fallbackRegistry.get(type));

    verify(registry).get(type);
  }

  @Test
  void testGet_Fallback(@Mock final Deserializer<String> deserializer) {
    final ParameterizedType<String> type = ParameterizedType.of(String.class);

    when(registry.get(type)).thenReturn(Optional.empty());
    when(fallback.get(type)).thenReturn(Optional.of(deserializer));

    assertEquals(Optional.of(deserializer), fallbackRegistry.get(type));

    verify(registry).get(type);
    verify(fallback).get(type);
  }
}
