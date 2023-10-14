package me.sparky983.warp.yaml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class PresentYamlConfigurationSourceTest {
  @Test
  void testNew_Null() {
    assertThrows(NullPointerException.class, () -> new PresentYamlConfigurationSource(null));
  }

  @Test
  void testNew() throws ConfigurationException {
    final ConfigurationNode.Map configuration = ConfigurationNodes.MIX;
    final YamlConfigurationSource source = new PresentYamlConfigurationSource(configuration);

    assertEquals(Optional.of(configuration), source.configuration());
  }
}
