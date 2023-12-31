package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
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

class BooleanDeserializerTest {
  @Test
  void testDeserialize_NonBoolean() {
    final ConfigurationBuilder<Configurations.Boolean> builder =
        Warp.builder(Configurations.Boolean.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group("property", ConfigurationError.error("Must be a boolean"))),
        thrown.errors());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testDeserialize(final boolean value) throws ConfigurationException {
    final ConfigurationBuilder<Configurations.Boolean> builder =
        Warp.builder(Configurations.Boolean.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.bool(value))
                        .build()));

    final Configurations.Boolean configuration = builder.build();

    assertEquals(value, configuration.property());
  }
}
