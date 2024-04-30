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
import me.sparky983.warp.Renderer;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class NestedConfigurationDeserializerTest {
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
                    ConfigurationNode.map(Map.entry("property", ConfigurationNode.nil()))));

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
                    ConfigurationNode.map(
                        Map.entry(
                            "property",
                            ConfigurationNode.map(
                                Map.entry("property", ConfigurationNode.nil()))))));

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
                    ConfigurationNode.map(
                        Map.entry(
                            "property",
                            ConfigurationNode.map(
                                Map.entry("property", ConfigurationNode.string("value")))))))
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
                    ConfigurationNode.map(
                        Map.entry(
                            "property",
                            ConfigurationNode.map(
                                Map.entry("property", ConfigurationNode.string("nested value")))))))
            .build();

    assertEquals("nested value", configuration.property().property());
  }
}
