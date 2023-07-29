package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class ListDeserializerTest {
  @Test
  void testDeserialize_NonList() {
    final ConfigurationBuilder<Configurations.StringList> builder =
        Warp.builder(Configurations.StringList.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(Set.of(ConfigurationError.of("Expected a list")), thrown.errors());
  }

  @Test
  void testDeserialize_NonDeserializableElement() {
    final ConfigurationBuilder<Configurations.NonDeserializableList> builder =
        Warp.builder(Configurations.NonDeserializableList.class);

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertEquals(
        Set.of(
            ConfigurationError.of(
                "Deserializer for the elements of java.util.List<java.util.Random> not found")),
        thrown.errors());
  }

  @Test
  void testDeserialize_Raw() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.RawList> builder =
        Warp.builder(Configurations.RawList.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.list(
                                ConfigurationNode.string("element 1"),
                                ConfigurationNode.string("element 2")))
                        .build()));

    final Configurations.RawList configuration = builder.build();

    assertEquals(
        List.of(ConfigurationNode.string("element 1"), ConfigurationNode.string("element 2")),
        configuration.property());
  }

  @Test
  void testDeserialize() throws ConfigurationException {
    final ConfigurationBuilder<Configurations.StringList> builder =
        Warp.builder(Configurations.StringList.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.list(
                                ConfigurationNode.string("element 1"),
                                ConfigurationNode.string("element 2")))
                        .build()));

    final Configurations.StringList configuration = builder.build();

    assertEquals(List.of("element 1", "element 2"), configuration.property());
  }
}