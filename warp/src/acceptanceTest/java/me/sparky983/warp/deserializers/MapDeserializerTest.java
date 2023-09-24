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

    assertEquals(
        Set.of(ConfigurationError.group("property", ConfigurationError.error("Must be a map"))),
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
