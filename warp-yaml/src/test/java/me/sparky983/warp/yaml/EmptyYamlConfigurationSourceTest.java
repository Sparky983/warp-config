package me.sparky983.warp.yaml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmptyYamlConfigurationSourceTest {
  YamlConfigurationSource source;

  @BeforeEach
  void setUp() {
    source = new EmptyYamlConfigurationSource();
  }

  @Test
  void testEmptyYamlConfigurationSource() throws ConfigurationException {
    assertEquals(Optional.empty(), source.configuration());
  }
}
