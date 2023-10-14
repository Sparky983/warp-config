package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import org.junit.jupiter.api.Test;

class WarpTest {
  @Test
  void testBuilder_Null() {
    assertThrows(NullPointerException.class, () -> Warp.builder(null));
  }

  @Test
  void testBuilder_NotAnnotated() {
    assertThrows(
        IllegalArgumentException.class, () -> Warp.builder(Configurations.MissingAnnotation.class));
  }

  @Test
  void testBuilder_NotPublic() {
    assertThrows(IllegalArgumentException.class, () -> Warp.builder(Configurations.Private.class));
  }

  @Test
  void testBuilder_NotInterface() {
    assertThrows(IllegalArgumentException.class, () -> Warp.builder(Configurations.Class.class));
  }

  @Test
  void testBuilder_Hidden() throws Exception {
    try (final InputStream inputStream =
        Configurations.Hidden.class
            .getClassLoader()
            .getResourceAsStream("me/sparky983/warp/Configurations$Hidden.class")) {
      final Class<?> hidden =
          MethodHandles.lookup()
              .defineHiddenClass(
                  inputStream.readAllBytes(), true, MethodHandles.Lookup.ClassOption.STRONG)
              .lookupClass();

      assertThrows(IllegalArgumentException.class, () -> Warp.builder(hidden));
    }
  }

  @Test
  void testBuilder_Sealed() {
    assertThrows(IllegalArgumentException.class, () -> Warp.builder(Configurations.Sealed.class));
  }

  @Test
  void testBuilder_Generic() {
    assertThrows(IllegalArgumentException.class, () -> Warp.builder(Configurations.Generic.class));
  }

  @Test
  void testBuilder_NonProperty() {
    assertThrows(
        IllegalArgumentException.class, () -> Warp.builder(Configurations.NonProperty.class));
  }

  // private property methods aren't tested since they're inaccessible

  @Test
  void testBuilder_StaticProperty() {
    assertThrows(
        IllegalArgumentException.class, () -> Warp.builder(Configurations.StaticProperty.class));
  }

  @Test
  void testBuilder_GenericProperty() {
    assertThrows(
        IllegalArgumentException.class, () -> Warp.builder(Configurations.GenericProperty.class));
  }

  @Test
  void testBuilder_ParameterizedProperty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Warp.builder(Configurations.ParameterizedProperty.class));
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
  void testProperty_NotExists() {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class);

    assertThrows(ConfigurationException.class, builder::build);
  }

  @Test
  void testNestedProperty_NotExists() {
    final ConfigurationBuilder<Configurations.Nested> builder =
        Warp.builder(Configurations.Nested.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("nested", ConfigurationNode.map().build())
                        .build()));

    assertThrows(ConfigurationException.class, builder::build);
  }

  @Test
  void testProperty_NotMap() {
    final ConfigurationBuilder<Configurations.Nested> builder =
        Warp.builder(Configurations.Nested.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("nested", ConfigurationNode.string("not a map"))
                        .build()));

    assertThrows(ConfigurationException.class, builder::build);
  }

  @Test
  void testUnserializableProperty() {
    final ConfigurationBuilder<Configurations.NonDeserializable> builder =
        Warp.builder(Configurations.NonDeserializable.class);

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testConflictingPropertyPaths() throws ConfigurationException {
    final Configurations.Conflicting builder =
        Warp.builder(Configurations.Conflicting.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.integer(10))
                        .build()))
            .build();

    assertEquals(10, builder.property1());
    assertEquals(10, builder.property2());
  }

  @Test
  void testSourcePrecedence() throws ConfigurationException {
    final Configurations.String builder =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.string("overrides"))
                        .build()))
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.string("overriden"))
                        .build()))
            .build();

    assertEquals("overrides", builder.property());
  }

  @Test
  void testSourceThrows() {
    final ConfigurationBuilder<Configurations.String> builder =
        Warp.builder(Configurations.String.class)
            .source(
                () -> {
                  throw new ConfigurationException();
                });

    assertThrows(ConfigurationException.class, builder::build);
  }

  @Test
  void testToString() throws ConfigurationException {
    final Configurations.String configuration =
        Warp.builder(Configurations.String.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map()
                        .entry("property", ConfigurationNode.string("value"))
                        .build()))
            .build();

    assertEquals("me.sparky983.warp.Configurations$String", configuration.toString());
  }

  @Test
  void testEquals_Null() throws ConfigurationException {
    final Configurations.Empty configuration = Warp.builder(Configurations.Empty.class).build();

    assertNotEquals(null, configuration);
  }

  @Test
  void testEquals_DifferentType() throws ConfigurationException {
    final Configurations.Empty configuration = Warp.builder(Configurations.Empty.class).build();

    assertNotEquals(new Object(), configuration);
  }

  @Test
  void testEquals_Different() throws ConfigurationException {
    final Configurations.Empty configuration1 = Warp.builder(Configurations.Empty.class).build();
    final Configurations.Empty configuration2 = Warp.builder(Configurations.Empty.class).build();

    assertNotEquals(configuration1, configuration2);
  }

  @Test
  void testEquals_Same() throws ConfigurationException {
    final Configurations.Empty configuration = Warp.builder(Configurations.Empty.class).build();

    assertEquals(configuration, configuration);
  }

  @Test
  void testHashCode_Same() throws ConfigurationException {
    final Configurations.Empty configuration = Warp.builder(Configurations.Empty.class).build();

    assertEquals(configuration.hashCode(), configuration.hashCode());
  }
  // TODO(Sparky983): figure out if identity hash code can have collisions, and write a test if not
}
