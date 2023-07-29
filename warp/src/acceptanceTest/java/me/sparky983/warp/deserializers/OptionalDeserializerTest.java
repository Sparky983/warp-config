package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class OptionalDeserializerTest {
  @Test
  void testDeserialize_NonDeserializableValue() {
    final ConfigurationBuilder<Configurations.NonDeserializableOptional> builder =
        Warp.builder(Configurations.NonDeserializableOptional.class)
            .source(ConfigurationSource.of(ConfigurationNode.map().entry("property", ConfigurationNode.string("value")).build()));

    final ConfigurationException thrown = assertThrows(ConfigurationException.class, builder::build);

    assertEquals(
        Set.of(
            ConfigurationError.of(
                "Deserializer for the value of java.util.Optional<java.util.Random> not found")),
        thrown.errors());
  }

  @Test
  void testDeserialize_Raw() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.RawOptional> builder =
        Warp.builder(Configurations.RawOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.string("value"))
                        .build()));

    final Configurations.RawOptional configuration = builder.build();

    assertEquals(Optional.of(ConfigurationNode.string("value")), configuration.property());
  }

  @Test
  void testDeserialize_Nil() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.RawOptional> builder =
        Warp.builder(Configurations.RawOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final Configurations.RawOptional configuration = builder.build();

    assertEquals(Optional.empty(), configuration.property());
  }

  @Test
  void testDeserialize() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.StringOptional> builder =
        Warp.builder(Configurations.StringOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.string("value"))
                        .build()));

    final Configurations.StringOptional configuration = builder.build();

    assertEquals(Optional.of("value"), configuration.property());
  }
}
