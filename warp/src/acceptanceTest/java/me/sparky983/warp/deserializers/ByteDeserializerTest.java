package me.sparky983.warp.deserializers;

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

class ByteDeserializerTest {
  @Test
  void testDeserialize_NonInteger() {
    final ConfigurationBuilder<Configurations.Byte> builder =
        Warp.builder(Configurations.Byte.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(
        Set.of(
            ConfigurationError.group("property", ConfigurationError.error("Must be an integer"))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(ints = {-129, 128})
  void testDeserialize_OutOfRange(final int value) {
    final ConfigurationBuilder<Configurations.Byte> builder =
        Warp.builder(Configurations.Byte.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(value))
                        .build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(
        Set.of(
            ConfigurationError.group(
                "property",
                ConfigurationError.error(
                    "Must be between "
                        + Byte.MIN_VALUE
                        + " and "
                        + Byte.MAX_VALUE
                        + " (both inclusive)"))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(bytes = {Byte.MIN_VALUE, 0, Byte.MAX_VALUE})
  void testDeserialize(final byte value) throws ConfigurationException {
    final ConfigurationBuilder<Configurations.Byte> builder =
        Warp.builder(Configurations.Byte.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(value))
                        .build()));

    final Configurations.Byte configuration = builder.build();

    assertEquals(value, configuration.property());
  }
}
