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
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class MapDeserializerTest {
  @Test
  void testFactory_NonDeserializableKey() {
    final ConfigurationBuilder<Configurations.NonDeserializableKeyMap> builder =
        Warp.builder(Configurations.NonDeserializableKeyMap.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testFactory_NonDeserializableValue() {
    final ConfigurationBuilder<Configurations.NonDeserializableValueMap> builder =
        Warp.builder(Configurations.NonDeserializableValueMap.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testFactory_Raw() {
    final ConfigurationBuilder<Configurations.RawMap> builder =
        Warp.builder(Configurations.RawMap.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialize_NonMap() {
    final ConfigurationBuilder<Configurations.StringStringMap> builder =
        Warp.builder(Configurations.StringStringMap.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map().entry("property", ConfigurationNode.nil()).build()));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(ConfigurationError.group("property", ConfigurationError.error("Must be a map"))),
        thrown.errors());
  }

  @Test
  void testDeserialize_NestedNonDeserializable() {
    final ConfigurationBuilder<Configurations.IntegerStringMap> builder =
        Warp.builder(Configurations.IntegerStringMap.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry(
                            "property",
                            ConfigurationNode.map()
                                .entry("1", ConfigurationNode.nil())
                                .entry("not integer", ConfigurationNode.nil())
                                .build())
                        .build()))
            .deserializer(
                Integer.class,
                (node, context) -> {
                  if (!(node instanceof final ConfigurationNode.String string)) {
                    throw new DeserializationException();
                  }
                  try {
                    return Renderer.of(Integer.parseInt(string.value()));
                  } catch (final NumberFormatException e) {
                    throw new DeserializationException(ConfigurationError.error("Cannot parse"));
                  }
                });

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "property",
                ConfigurationError.group("1", ConfigurationError.error("Must be a string")),
                ConfigurationError.group(
                    "not integer",
                    ConfigurationError.error("Cannot parse"),
                    ConfigurationError.error("Must be a string")))),
        thrown.errors());
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

    final Map<String, String> property = configuration.property();

    assertEquals(Map.of("key 1", "value 1", "key 2", "value 2"), property);
    assertThrows(UnsupportedOperationException.class, () -> property.put("key 3", "value 3"));
  }
}
