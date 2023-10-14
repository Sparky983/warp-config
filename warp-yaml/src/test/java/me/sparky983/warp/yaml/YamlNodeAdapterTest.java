package me.sparky983.warp.yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class YamlNodeAdapterTest {
  @Test
  void testAdapt_Null() {
    assertThrows(NullPointerException.class, () -> YamlNodeAdapter.adapt(null));
  }

  @Test
  void testAdapt_InvalidNode() {
    final YamlMapping mapping =
        Yaml.createYamlMappingBuilder()
            .add("invalid", Yaml.createYamlStreamBuilder().build())
            .build();

    assertThrows(IllegalArgumentException.class, () -> YamlNodeAdapter.adapt(mapping));
  }

  @Test
  void testAdapt_NonStringKey() {
    final YamlMapping mapping =
        Yaml.createYamlMappingBuilder()
            .add(Yaml.createYamlSequenceBuilder().add("element").build(), "value")
            .build();

    assertThrows(IllegalArgumentException.class, () -> YamlNodeAdapter.adapt(mapping));
  }

  @Test
  void testNode() {
    final ConfigurationNode node =
        YamlNodeAdapter.adapt(
            Yaml.createYamlMappingBuilder()
                .add("no value", (YamlNode) null)
                .add("null", "null")
                .add("true", "true")
                .add("false", "false")
                .add("integer", "10")
                .add("decimal", "10.0")
                .add("string", "some string")
                .add("list", Yaml.createYamlSequenceBuilder().add("10").add("some string").build())
                .add("map", Yaml.createYamlMappingBuilder().add("key", "value").build())
                .build());

    assertEquals(ConfigurationNodes.MIX, node);
  }
}
