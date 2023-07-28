package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ShortDeserializerTest {
  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationBuilder<Configurations.Short> builder =
        Warp.builder(Configurations.Short.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(Set.of(ConfigurationError.of("Expected an integer")), thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(ints = {-32769, 32768})
  void testDeserialize_OutOfRange(final int value) {
    final ConfigurationBuilder<Configurations.Short> builder =
        Warp.builder(Configurations.Short.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(value))
                        .build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(
        Set.of(
            ConfigurationError.of(
                String.format("Expected property to be between %s and %s (was %s)", Short.MIN_VALUE, Short.MAX_VALUE, value))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(ints = {Short.MIN_VALUE, 0, Short.MAX_VALUE})
  void testDeserialize(final int value) throws ConfigurationException {
    final ConfigurationBuilder<Configurations.Short> builder =
        Warp.builder(Configurations.Short.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(value))
                        .build()));

    final Configurations.Short configuration = builder.build();

    assertEquals(value, configuration.property());
  }
}
