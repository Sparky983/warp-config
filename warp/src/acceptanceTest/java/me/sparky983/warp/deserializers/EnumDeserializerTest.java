package me.sparky983.warp.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class EnumDeserializerTest {
  @Test
  void testFactory_EnumExact() {
    final ConfigurationBuilder<Configurations.EnumExact> builder =
        Warp.builder(Configurations.EnumExact.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialize_NonString() {
    final ConfigurationBuilder<Configurations.Enum> builder =
        Warp.builder(Configurations.Enum.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.entry("property", ConfigurationNode.nil()))));

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, builder::build);

    assertIterableEquals(
        List.of(ConfigurationError.group("property", ConfigurationError.error("Must be a string"))),
        thrown.errors());
  }

  @Test
  void testDeserialize_IllegalValue() {
    final ConfigurationBuilder<Configurations.Enum> builder =
        Warp.builder(Configurations.Enum.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(
                        Map.entry("property", ConfigurationNode.string("ILLEGAL_VALUE")))));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);

    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "property", ConfigurationError.error("ILLEGAL_VALUE is not a valid value"))),
        thrown.errors());
  }

  @Test
  void testDeserialize() throws ConfigurationException {
    final Configurations.Enum configuration =
        Warp.builder(Configurations.Enum.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(
                        Map.entry("property", ConfigurationNode.string("RUNTIME")))))
            .build();

    assertEquals(RetentionPolicy.RUNTIME, configuration.property());
  }
}
