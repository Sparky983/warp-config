package me.sparky983.warp.yaml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class OkYamlConfigurationSourceTest {
  @Test
  void testNew_Null() {
    assertThrows(NullPointerException.class, () -> new OkYamlConfigurationSource(null));
  }

  @Test
  void testNew_Present() throws ConfigurationException {
    final ConfigurationNode configuration = ConfigurationNode.string("present");
    final YamlConfigurationSource source =
        new OkYamlConfigurationSource(Optional.of(configuration));

    assertEquals(Optional.of(configuration), source.configuration());
  }

  @Test
  void testNew_Empty() throws ConfigurationException {
    final YamlConfigurationSource source = new OkYamlConfigurationSource(Optional.empty());

    assertEquals(Optional.empty(), source.configuration());
  }
}
