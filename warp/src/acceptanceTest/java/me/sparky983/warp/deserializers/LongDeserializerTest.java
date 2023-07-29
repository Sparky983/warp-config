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

class LongDeserializerTest {
  @Test
  void testDeserialize_NonLong() {
    final ConfigurationBuilder<Configurations.Long> builder =
        Warp.builder(Configurations.Long.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(Set.of(ConfigurationError.of("Expected an integer")), thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(longs = {Long.MIN_VALUE, 0, Long.MAX_VALUE})
  void testDeserialize(final long value) throws ConfigurationException {
    final ConfigurationBuilder<Configurations.Long> builder =
        Warp.builder(Configurations.Long.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(value))
                        .build()));

    final Configurations.Long configuration = builder.build();

    assertEquals(value, configuration.property());
  }
}
