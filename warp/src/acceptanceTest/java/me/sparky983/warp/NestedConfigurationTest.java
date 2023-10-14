package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class NestedConfigurationTest {
  @Test
  void testInvalid() {
    final ConfigurationBuilder<Configurations.NestedSealed> builder =
        Warp.builder(Configurations.NestedSealed.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testNonMap() {
    final ConfigurationBuilder<Configurations.NestedString> configuration =
        Warp.builder(Configurations.NestedString.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, configuration::build);

    assertIterableEquals(
        List.of(ConfigurationError.group("property", ConfigurationError.error("Must be a map"))),
        thrown.errors());
  }

  @Test
  void testNestedNonDeserializable() {
    final ConfigurationBuilder<Configurations.NestedString> configuration =
        Warp.builder(Configurations.NestedString.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.map()
                                .entry("property", ConfigurationNode.nil())
                                .build())
                        .build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, configuration::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "property",
                ConfigurationError.group(
                    "property", ConfigurationError.error("Must be a string")))),
        thrown.errors());
  }

  @Test
  void testNested() throws ConfigurationException {
    final Configurations.NestedString configuration =
        Warp.builder(Configurations.NestedString.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.map()
                                .entry("property", ConfigurationNode.string("nested value"))
                                .build())
                        .build()))
            .build();

    assertEquals("nested value", configuration.property().property());
  }
}
