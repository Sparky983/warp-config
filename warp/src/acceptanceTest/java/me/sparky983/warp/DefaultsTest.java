package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

class DefaultsTest {
  @Test
  void testDefault_Optional() throws ConfigurationException {
    final Configurations.StringOptional configuration = Warp.builder(Configurations.StringOptional.class)
        .source(ConfigurationSource.blank())
        .build();

    assertEquals(Optional.empty(), configuration.property());
  }

  @Test
  void testDefault_List() throws ConfigurationException {
    final Configurations.StringList configuration = Warp.builder(Configurations.StringList.class)
        .source(ConfigurationSource.blank())
        .build();

    assertEquals(List.of(), configuration.property());
  }

  @Test
  void testDefault_Map() throws ConfigurationException {
    final Configurations.StringStringMap configuration = Warp.builder(Configurations.StringStringMap.class)
        .source(ConfigurationSource.blank())
        .build();

    assertEquals(Map.of(), configuration.property());
  }
}
