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
import me.sparky983.warp.Renderer;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class NestedConfigurationDeserializerTest {
  @Test
  void testInvalid() {
    final Warp.Builder<Configurations.NestedSealed> builder =
        Warp.builder(Configurations.NestedSealed.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testNonMap() {
    final Warp.Builder<Configurations.NestedString> configuration =
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
    final Warp.Builder<Configurations.NestedString> configuration =
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
  void testNested_CustomDeserializedProperty() throws ConfigurationException {
    final Configurations.NestedString configuration =
        Warp.builder(Configurations.NestedString.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.map()
                                .entry("property", ConfigurationNode.string("value"))
                                .build())
                        .build()))
            .deserializer(
                String.class,
                (node, context) -> {
                  assertEquals(node, ConfigurationNode.string("value"));
                  return Renderer.of("custom value");
                })
            .build();

    assertEquals("custom value", configuration.property().property());
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
