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

class DoubleDeserializerTest {
  @Test
  void testDeserialize_NonNumber() {
    final Warp.Builder<Configurations.Double> builder =
        Warp.builder(Configurations.Double.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(ConfigurationError.group("property", ConfigurationError.error("Must be a number"))),
        thrown.errors());
  }

  @Test
  void testDeserialize_Integer() throws ConfigurationException {
    final Warp.Builder<Configurations.Double> builder =
        Warp.builder(Configurations.Double.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(1))
                        .build()));

    final Configurations.Double configuration = builder.build();

    assertEquals(1.0, configuration.property());
  }

  @Test
  void testDeserialize_Decimal() throws ConfigurationException {
    final Warp.Builder<Configurations.Double> builder =
        Warp.builder(Configurations.Double.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.decimal(1.5))
                        .build()));

    final Configurations.Double configuration = builder.build();

    assertEquals(1.5, configuration.property());
  }
}
