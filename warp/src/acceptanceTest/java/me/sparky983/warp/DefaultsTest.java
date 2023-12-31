package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DefaultsTest {
  @Test
  void testDefault_Optional() throws ConfigurationException {
    final Configurations.StringOptional configuration =
        Warp.builder(Configurations.StringOptional.class).build();

    assertEquals(Optional.empty(), configuration.property());
  }

  @Test
  void testDefault_List() throws ConfigurationException {
    final Configurations.StringList configuration =
        Warp.builder(Configurations.StringList.class).build();

    assertEquals(List.of(), configuration.property());
  }

  @Test
  void testDefault_Map() throws ConfigurationException {
    final Configurations.StringStringMap configuration =
        Warp.builder(Configurations.StringStringMap.class).build();

    assertEquals(Map.of(), configuration.property());
  }
}
