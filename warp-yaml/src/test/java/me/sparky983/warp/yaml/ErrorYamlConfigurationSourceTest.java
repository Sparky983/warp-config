package me.sparky983.warp.yaml;

import static org.junit.jupiter.api.Assertions.*;

import me.sparky983.warp.ConfigurationException;
import org.junit.jupiter.api.Test;

class ErrorYamlConfigurationSourceTest {
  @Test
  void testNew_Null() {
    assertThrows(NullPointerException.class, () -> new ErrorYamlConfigurationSource(null));
  }

  @Test
  void testNew() {
    final ConfigurationException exception = new ConfigurationException();
    final YamlConfigurationSource source = new ErrorYamlConfigurationSource(exception);

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, source::configuration);

    assertEquals(exception, thrown);
  }
}
