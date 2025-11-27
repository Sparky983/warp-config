package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FallbackMethodsTest {
  @Test
  void testOverriddenDefault_String() throws ConfigurationException {
    final Configurations.DefaultString configuration =
        Warp.builder(Configurations.DefaultString.class).build();

    assertEquals("<default>", configuration.property());
  }

  @Test
  void testOverriddenDefault_List() throws ConfigurationException {
    final Configurations.DefaultList configuration =
        Warp.builder(Configurations.DefaultList.class).build();

    assertEquals(List.of("<default>"), configuration.property());
  }

  @Test
  void testOverriddenDefault_Map() throws ConfigurationException {
    final Configurations.DefaultMap configuration =
        Warp.builder(Configurations.DefaultMap.class).build();

    assertEquals(Map.of("<default-key>", "<default-value>"), configuration.property());
  }

  @Test
  void testOverriddenDefault_Optional() throws ConfigurationException {
    // Not sure why you would want to do this, but test for consistency
    final Configurations.DefaultOptional configuration =
        Warp.builder(Configurations.DefaultOptional.class).build();

    assertEquals(Optional.of("<default>"), configuration.property());
  }

  @Test
  void testOverriddenDefault_Throws() throws ConfigurationException {
    final Configurations.DefaultThrowing configuration =
        Warp.builder(Configurations.DefaultThrowing.class).build();

    final Throwable thrown = assertThrows(Throwable.class, configuration::property);
    assertEquals(Configurations.DefaultThrowing.EXCEPTION, thrown);
  }
}
