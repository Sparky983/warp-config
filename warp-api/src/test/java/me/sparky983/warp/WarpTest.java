package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.annotations.Property;
import org.junit.jupiter.api.Test;

class WarpTest {
  @Test
  void testBuilder_Null() {
    assertThrows(NullPointerException.class, () -> Warp.builder(null));
  }

  @Test
  void testBuilder_NotConfigurationClass() {
    interface TestConfiguration {}

    assertThrows(IllegalArgumentException.class, () -> Warp.builder(TestConfiguration.class));
  }

  @Test
  void testSource_Null() {
    final var builder = Warp.builder(EmptyConfiguration.class);

    assertThrows(NullPointerException.class, () -> builder.source(null));
  }

  @Test
  void testHashCode_Self() {
    final var configuration = Warp.builder(EmptyConfiguration.class);

    assertEquals(configuration.hashCode(), configuration.hashCode());
  }

  @Test
  void testHashCode_Other() {
    final var configuration1 = Warp.builder(EmptyConfiguration.class);
    final var configuration2 = Warp.builder(EmptyConfiguration.class);

    assertNotEquals(configuration1.hashCode(), configuration2.hashCode());
  }

  @SuppressWarnings("EqualsWithItself")
  @Test
  void testEquals_Self() {
    final var configuration = Warp.builder(EmptyConfiguration.class);

    assertEquals(configuration, configuration);
  }

  @Test
  void testEquals_Other() {
    final var configuration1 = Warp.builder(EmptyConfiguration.class);
    final var configuration2 = Warp.builder(EmptyConfiguration.class);

    assertNotEquals(configuration1, configuration2);
  }

  @Test
  void testStringProperty_Exists() {
    @Configuration
    interface TestConfiguration {
      @Property("test.property")
      String property();
    }
    final var configuration = Warp.builder(TestConfiguration.class)
        .source(ConfigurationSource.of(ConfigurationValue.map()
            .entry("test", ConfigurationValue.map()
                .entry("property", ConfigurationValue.primitive("Some value"))
                .build())
            .build()))
        .build();

    assertEquals("Some value", configuration.property());
  }
}
