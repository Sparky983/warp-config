package me.sparky983.warp;

import static me.sparky983.warp.ConfigurationValue.map;
import static me.sparky983.warp.ConfigurationValue.primitive;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WarpTest {
  @Test
  void testBuilder_Null() {
    assertThrows(NullPointerException.class, () -> Warp.builder(null));
  }

  @Test
  void testBuilder_NotConfigurationClass() {
    assertThrows(IllegalArgumentException.class, () -> Warp.builder(Configurations.Invalid.class));
  }

  @Test
  void testSource_Null() {
    final var builder = Warp.builder(Configurations.Empty.class);

    assertThrows(NullPointerException.class, () -> builder.source(null));
  }

  @Test
  void testHashCode_Self() {
    final var configuration = Warp.builder(Configurations.Empty.class);

    assertEquals(configuration.hashCode(), configuration.hashCode());
  }

  @Test
  void testHashCode_Other() {
    final var configuration1 = Warp.builder(Configurations.Empty.class);
    final var configuration2 = Warp.builder(Configurations.Empty.class);

    assertNotEquals(configuration1.hashCode(), configuration2.hashCode());
  }

  @SuppressWarnings("EqualsWithItself")
  @Test
  void testEquals_Self() {
    final var configuration = Warp.builder(Configurations.Empty.class);

    assertEquals(configuration, configuration);
  }

  @Test
  void testEquals_Other() {
    final var configuration1 = Warp.builder(Configurations.Empty.class);
    final var configuration2 = Warp.builder(Configurations.Empty.class);

    assertNotEquals(configuration1, configuration2);
  }

  @Test
  void testProperty_NotExists() {
    final var builder =
        Warp.builder(Configurations.String.class).source(ConfigurationSource.empty());

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialization() { // TODO(Sparky983): We definitely need more deserialization tests
    final var configuration =
        Warp.builder(Configurations.Int.class)
            .source(ConfigurationSource.of(map().entry("property", primitive("10")).build()))
            .build();

    assertEquals(10, configuration.property());
  }

  @Test
  void testStringProperty() {
    final var configuration =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(map().entry("property", primitive("Some value")).build()))
            .build();

    assertEquals("Some value", configuration.property());
  }
}
