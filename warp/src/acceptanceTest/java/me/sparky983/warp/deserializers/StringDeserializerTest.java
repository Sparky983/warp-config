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

class StringDeserializerTest {
  @Test
  void testDeserialize_NonString() {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(Set.of(ConfigurationError.of("Expected a string")), thrown.errors());
  }

  @Test
  void testDeserialize() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.string("value"))
                        .build()));

    final Configurations.String configuration = builder.build();

    assertEquals("value", configuration.property());
  }
}
