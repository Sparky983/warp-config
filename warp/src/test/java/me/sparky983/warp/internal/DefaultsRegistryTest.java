package me.sparky983.warp.internal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
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
    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> defaultsRegistry.register(null, node));
  }

  @Test
  void testRegister_NullNode() {
    assertThrows(NullPointerException.class, () -> defaultsRegistry.register(String.class, null));
  }

  @Test
  void testGet_Null() {
    assertThrows(NullPointerException.class, () -> defaultsRegistry.get(null));
  }

  @Test
  void testGet_NotRegistered() {
    final Optional<ConfigurationNode> node = defaultsRegistry.get(String.class);

    assertEquals(Optional.empty(), node);
  }

  @Test
  void testGet_Registered() {
    final ConfigurationNode node = ConfigurationNode.nil();
    defaultsRegistry.register(String.class, node);

    final Optional<ConfigurationNode> result = defaultsRegistry.get(String.class);

    assertEquals(Optional.of(node), result);
  }
}
