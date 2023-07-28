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

class IntegerDeserializerTest {
  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationBuilder<Configurations.Integer> builder =
        Warp.builder(Configurations.Integer.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(Set.of(ConfigurationError.of("Expected an integer")), thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(longs = {-2147483649L, 2147483648L})
  void testDeserialize_OutOfRange(final long value) {
    final ConfigurationBuilder<Configurations.Integer> builder =
        Warp.builder(Configurations.Integer.class)
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
                String.format("Expected property to be between %s and %s (was %s)", Integer.MIN_VALUE, Integer.MAX_VALUE, value))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE})
  void testDeserialize(final int value) throws ConfigurationException {
    final ConfigurationBuilder<Configurations.Integer> builder =
        Warp.builder(Configurations.Integer.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(value))
                        .build()));

    final Configurations.Integer configuration = builder.build();

    assertEquals(value, configuration.property());
  }
}
