package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Set;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class MapDeserializerTest {
  @Test
  void testDeserialize_NonMap() {
    final ConfigurationBuilder<Configurations.StringStringMap> builder =
        Warp.builder(Configurations.StringStringMap.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(Set.of(ConfigurationError.of("Must be a map")), thrown.errors());
  }

  @Test
  void testDeserialize_NonDeserializableKey() {
    final ConfigurationBuilder<Configurations.NonDeserializableKeyMap> builder =
        Warp.builder(Configurations.NonDeserializableKeyMap.class);

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(
        Set.of(
            ConfigurationError.of(
                "Deserializer for the keys of java.util.Map<java.util.Random, java.lang.String> not found")),
        thrown.errors());
  }

  @Test
  void testDeserialize_NonDeserializableValue() {
    final ConfigurationBuilder<Configurations.NonDeserializableValueMap> builder =
        Warp.builder(Configurations.NonDeserializableValueMap.class);

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(
        Set.of(
            ConfigurationError.of(
                "Deserializer for the values of java.util.Map<java.lang.String, java.util.Random> not found")),
        thrown.errors());
  }

  @Test
  void testDeserialize_Raw() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.RawMap> builder =
        Warp.builder(Configurations.RawMap.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.map()
                                .entry("key 1", ConfigurationNode.string("value 1"))
                                .entry("key 2", ConfigurationNode.string("value 2"))
                                .build())
                        .build()));

    final Configurations.RawMap configuration = builder.build();

    assertEquals(
        Map.of(
            "key 1",
            ConfigurationNode.string("value 1"),
            "key 2",
            ConfigurationNode.string("value 2")),
        configuration.property());
  }

  @Test
  void testDeserialize() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.StringStringMap> builder =
        Warp.builder(Configurations.StringStringMap.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.map()
                                .entry("key 1", ConfigurationNode.string("value 1"))
                                .entry("key 2", ConfigurationNode.string("value 2"))
                                .build())
                        .build()));

    final Configurations.StringStringMap configuration = builder.build();

    assertEquals(Map.of("key 1", "value 1", "key 2", "value 2"), configuration.property());
  }
}
