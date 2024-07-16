package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class OptionalDeserializerTest {
  @Test
  void testFactory_NonDeserializableValue() {
    final Warp.Builder<Configurations.NonDeserializableOptional> builder =
        Warp.builder(Configurations.NonDeserializableOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.string("value"))
                        .build()));

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testFactory_Raw() {
    final Warp.Builder<Configurations.RawOptional> builder =
        Warp.builder(Configurations.RawOptional.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialize_Nil() throws ConfigurationException {
    final Warp.Builder<Configurations.StringOptional> builder =
        Warp.builder(Configurations.StringOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final Configurations.StringOptional configuration = builder.build();

    assertEquals(Optional.empty(), configuration.property());
  }

  @Test
  void testDeserialize() throws ConfigurationException {
    final Warp.Builder<Configurations.StringOptional> builder =
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
