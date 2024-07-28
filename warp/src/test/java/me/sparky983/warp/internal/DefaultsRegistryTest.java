package me.sparky983.warp.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import me.sparky983.warp.Renderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultsRegistryTest {
  DefaultsRegistry defaultsRegistry;

  @BeforeEach
  void setUp() {
    defaultsRegistry = DefaultsRegistry.create();
  }

  @Test
  void testRegister_NullType() {
    final Renderer<Integer> renderer = Renderer.of(0);

    assertThrows(NullPointerException.class, () -> defaultsRegistry.register(null, renderer));
  }

  @Test
  void testRegister_NullRenderer() {
    assertThrows(NullPointerException.class, () -> defaultsRegistry.register(Integer.class, null));
  }

  @Test
  void testRegister_AlreadyRegistered() {
    final Renderer<Integer> renderer1 = Renderer.of(0);
    final Renderer<Integer> renderer2 = Renderer.of(0);

    defaultsRegistry.register(Integer.class, renderer1);

    assertThrows(
        IllegalStateException.class, () -> defaultsRegistry.register(Integer.class, renderer2));
    assertEquals(Optional.of(renderer1), defaultsRegistry.get(Integer.class));
  }

  @Test
  void testRegister() {
    final Renderer<Integer> renderer = Renderer.of(0);

    defaultsRegistry.register(Integer.class, renderer);

    assertEquals(Optional.of(renderer), defaultsRegistry.get(Integer.class));
  }

  @Test
  void testGet_Null() {
    assertThrows(NullPointerException.class, () -> defaultsRegistry.get(null));
  }

  @Test
  void testGet_NotRegistered() {
    final Optional<Renderer<? extends Integer>> renderer = defaultsRegistry.get(Integer.class);

    assertEquals(Optional.empty(), renderer);
  }

  @Test
  void testGet_Registered() {
    final Renderer<Integer> renderer = Renderer.of(0);
    defaultsRegistry.register(Integer.class, renderer);

    final Optional<Renderer<? extends Integer>> result = defaultsRegistry.get(Integer.class);

    assertEquals(Optional.of(renderer), result);
  }
}
