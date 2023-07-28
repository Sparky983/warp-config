package me.sparky983.warp;

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
    final ConfigurationBuilder<Configurations.Empty> builder =
        Warp.builder(Configurations.Empty.class);

    assertThrows(NullPointerException.class, () -> builder.source(null));
  }

  @Test
  void testHashCode_Self() {
    final ConfigurationBuilder<Configurations.Empty> configuration =
        Warp.builder(Configurations.Empty.class);

    assertEquals(configuration.hashCode(), configuration.hashCode());
  }

  @Test
  void testHashCode_Other() {
    final ConfigurationBuilder<Configurations.Empty> configuration1 =
        Warp.builder(Configurations.Empty.class);
    final ConfigurationBuilder<Configurations.Empty> configuration2 =
        Warp.builder(Configurations.Empty.class);

    assertNotEquals(configuration1.hashCode(), configuration2.hashCode());
  }

  @SuppressWarnings("EqualsWithItself")
  @Test
  void testEquals_Self() {
    final ConfigurationBuilder<Configurations.Empty> configuration =
        Warp.builder(Configurations.Empty.class);

    assertEquals(configuration, configuration);
  }

  @Test
  void testEquals_Other() {
    final ConfigurationBuilder<Configurations.Empty> configuration1 =
        Warp.builder(Configurations.Empty.class);
    final ConfigurationBuilder<Configurations.Empty> configuration2 =
        Warp.builder(Configurations.Empty.class);

    assertNotEquals(configuration1, configuration2);
  }

  @Test
  void testProperty_NotExists() {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class).source(ConfigurationSource.empty());

    assertThrows(ConfigurationException.class, builder::build);
  }

  @Test
  void testUnserializableProperty() {
    final ConfigurationBuilder<Configurations.Unserializable> builder =
        Warp.builder(Configurations.Unserializable.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  // TODO(Sparky983): We definitely need more deserialization tests
  void testDeserialization() throws ConfigurationException {
    final Configurations.Int configuration =
        Warp.builder(Configurations.Int.class)
            .source(ConfigurationSource.of(ConfigurationNode.map().entry("property", ConfigurationNode.integer(10)).build()))
            .build();

    assertEquals(10, configuration.property());
  }

  @Test
  void testStringProperty() throws ConfigurationException {
    final Configurations.String configuration =
        Warp.builder(Configurations.String.class)
            .source(ConfigurationSource.of(ConfigurationNode.map().entry("property", ConfigurationNode.string("Some value")).build()))
            .build();

    assertEquals("Some value", configuration.property());
  }
}
