package me.sparky983.warp.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.Warp;
import org.junit.jupiter.api.Test;

class CompositeNodeTest {
  @Test
  void testString_FirstString() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.string("first.property"))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.string("second.property"))
    );

    final Configurations.String configuration = Warp.builder(Configurations.String.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals("first.property", configuration.property());
  }

  @Test
  void testString_SecondString() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.nil())
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.string("second.property"))
    );

    final Configurations.String configuration = Warp.builder(Configurations.String.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals("second.property", configuration.property());
  }

  @Test
  void testDecimal_FirstDecimal() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.decimal(1.0))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.decimal(2.0))
    );

    final Configurations.Double configuration = Warp.builder(Configurations.Double.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(1.0, configuration.property());
  }

  @Test
  void testDecimal_SecondDecimal() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.nil())
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.decimal(2.0))
    );

    final Configurations.Double configuration = Warp.builder(Configurations.Double.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(2.0, configuration.property());
  }

  @Test
  void testInteger_FirstInteger() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.integer(1))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.integer(2))
    );

    final Configurations.Integer configuration = Warp.builder(Configurations.Integer.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(1, configuration.property());
  }

  @Test
  void testInteger_SecondInteger() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.nil())
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.integer(2))
    );

    final Configurations.Integer configuration = Warp.builder(Configurations.Integer.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(2, configuration.property());
  }

  @Test
  void testBoolean_FirstBoolean() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.bool(true))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.bool(false))
    );

    final Configurations.Boolean configuration = Warp.builder(Configurations.Boolean.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertTrue(configuration.property());
  }

  @Test
  void testBoolean_SecondBoolean() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.nil())
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.bool(false))
    );

    final Configurations.Boolean configuration = Warp.builder(Configurations.Boolean.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertFalse(configuration.property());
  }

  @Test
  void testNil_FirstNil() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.nil())
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.nil())
    );

    final Configurations.StringOptional configuration = Warp.builder(Configurations.StringOptional.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(Optional.empty(), configuration.property());
  }

  @Test
  void testNil_SecondNil() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.string("non-nil"))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.nil())
    );

    final Configurations.StringOptional configuration = Warp.builder(Configurations.StringOptional.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(Optional.empty(), configuration.property());
  }

  @Test
  void testList_FirstList() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.list(
            ConfigurationNode.string("first.property[0]")
        ))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.list(
            ConfigurationNode.string("second.property[0]")
        ))
    );

    final Configurations.StringList configuration = Warp.builder(Configurations.StringList.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(List.of("first.property[0]"), configuration.property());
  }

  @Test
  void testList_SecondList() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.string("non-list"))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("property", ConfigurationNode.list(
            ConfigurationNode.string("second.property[0]")
        ))
    );

    final Configurations.StringList configuration = Warp.builder(Configurations.StringList.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals(List.of("second.property[0]"), configuration.property());
  }

  @Test
  void testMapMerge() throws ConfigurationException {
    final ConfigurationNode first = ConfigurationNode.map(
        Map.entry("first", ConfigurationNode.string("first.first")),
        Map.entry("both", ConfigurationNode.string("first.both")),
        Map.entry("nested", ConfigurationNode.map(
            Map.entry("first", ConfigurationNode.string("first.nested.first")),
            Map.entry("both", ConfigurationNode.string("first.nested.both"))
        ))
    );
    final ConfigurationNode second = ConfigurationNode.map(
        Map.entry("second", ConfigurationNode.string("second.second")),
        Map.entry("both", ConfigurationNode.string("second.both")),
        Map.entry("nested", ConfigurationNode.map(
            Map.entry("second", ConfigurationNode.string("second.nested.second")),
            Map.entry("both", ConfigurationNode.string("second.nested.both"))
        ))
    );

    final Configurations.Combined configuration = Warp.builder(Configurations.Combined.class)
        .source(ConfigurationSource.of(first))
        .source(ConfigurationSource.of(second))
        .build();

    assertEquals("first.first", configuration.first());
    assertEquals("second.second", configuration.second());
    assertEquals("first.both", configuration.both());
    assertEquals("first.nested.first", configuration.nested().first());
    assertEquals("second.nested.second", configuration.nested().second());
    assertEquals("first.nested.both", configuration.nested().both());
  }
}
