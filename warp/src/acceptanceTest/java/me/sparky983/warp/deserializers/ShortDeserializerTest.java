package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
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
    final Warp.Builder<Configurations.Short> builder =
        Warp.builder(Configurations.Short.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group("property", ConfigurationError.error("Must be an integer"))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(ints = {-32769, 32768})
  void testDeserialize_OutOfRange(final int value) {
    final Warp.Builder<Configurations.Short> builder =
        Warp.builder(Configurations.Short.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(value))
                        .build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "property",
                ConfigurationError.error(
                    "Must be between "
                        + Short.MIN_VALUE
                        + " and "
                        + Short.MAX_VALUE
                        + " (both inclusive)"))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(shorts = {Short.MIN_VALUE, 0, Short.MAX_VALUE})
  void testDeserialize(final short value) throws ConfigurationException {
    final Warp.Builder<Configurations.Short> builder =
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
