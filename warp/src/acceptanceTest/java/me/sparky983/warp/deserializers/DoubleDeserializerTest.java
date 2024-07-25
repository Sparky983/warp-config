package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import me.sparky983.warp.ConfigurationBuilder;
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
    final ConfigurationBuilder<Configurations.Double> builder =
        Warp.builder(Configurations.Double.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.entry("property", ConfigurationNode.nil()))));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group("property", ConfigurationError.error("Must be a decimal"))),
        thrown.errors());
  }

  @Test
  void testDeserialize_Decimal() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.Double> builder =
        Warp.builder(Configurations.Double.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.entry("property", ConfigurationNode.decimal(1.5)))));

    final Configurations.Double configuration = builder.build();

    assertEquals(1.5, configuration.property());
  }
}
