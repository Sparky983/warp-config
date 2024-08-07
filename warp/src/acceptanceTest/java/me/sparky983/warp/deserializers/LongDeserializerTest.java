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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LongDeserializerTest {
  @Test
  void testDeserialize_NonLong() {
    final ConfigurationBuilder<Configurations.Long> builder =
        Warp.builder(Configurations.Long.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.entry("property", ConfigurationNode.nil()))));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group("property", ConfigurationError.error("Must be an integer"))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(longs = {Long.MIN_VALUE, 0, Long.MAX_VALUE})
  void testDeserialize(final long value) throws ConfigurationException {
    final ConfigurationBuilder<Configurations.Long> builder =
        Warp.builder(Configurations.Long.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(
                        Map.entry("property", ConfigurationNode.integer(value)))));

    final Configurations.Long configuration = builder.build();

    assertEquals(value, configuration.property());
  }
}
