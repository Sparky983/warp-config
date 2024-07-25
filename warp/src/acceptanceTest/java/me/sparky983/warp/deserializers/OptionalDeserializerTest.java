package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class OptionalDeserializerTest {
  @Test
  void testFactory_NonDeserializableValue() {
    final ConfigurationBuilder<Configurations.NonDeserializableOptional> builder =
        Warp.builder(Configurations.NonDeserializableOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(
                        Map.entry("property", ConfigurationNode.string("value")))));

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testFactory_Raw() {
    final ConfigurationBuilder<Configurations.RawOptional> builder =
        Warp.builder(Configurations.RawOptional.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialize_Nil() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.StringOptional> builder =
        Warp.builder(Configurations.StringOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.entry("property", ConfigurationNode.nil()))));

    final Configurations.StringOptional configuration = builder.build();

    assertEquals(Optional.empty(), configuration.property());
  }

  @Test
  void testDeserialize() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.StringOptional> builder =
        Warp.builder(Configurations.StringOptional.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(
                        Map.entry("property", ConfigurationNode.string("value")))));

    final Configurations.StringOptional configuration = builder.build();

    assertEquals(Optional.of("value"), configuration.property());
  }
}
