package me.sparky983.warp.yaml;

import static me.sparky983.warp.yaml.ConfigurationNodes.nodeIsMix;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amihaiemil.eoyaml.Comment;
import com.amihaiemil.eoyaml.Node;
import com.amihaiemil.eoyaml.Scalar;
import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlStream;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import me.sparky983.warp.ConfigurationNode;
import org.junit.jupiter.api.Test;

class YamlNodeConverterTest {
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
    final Scalar nullValueScalar =
        new Scalar() {
          @Override
          public String value() {
            // eo-yaml has no way to create a scalar with a null value
            // this is returned by the library when a scalar has the literal value
            // "null"
            return null;
          }

          @Override
          public boolean isEmpty() {
            return true;
          }

          @Override
          public Comment comment() {
            final Scalar scalar = this;
            return new Comment() {
              @Override
              public YamlNode yamlNode() {
                return scalar;
              }

              @Override
              public String value() {
                return "";
              }
            };
          }

          @Override
          public Node type() {
            return Node.SCALAR;
          }

          @Override
          public Scalar asScalar() throws YamlReadingException, ClassCastException {
            return this;
          }

          @Override
          public YamlMapping asMapping() {
            throw new ClassCastException();
          }

          @Override
          public YamlSequence asSequence() {
            throw new ClassCastException();
          }

          @Override
          public YamlStream asStream() {
            throw new ClassCastException();
          }

          @Override
          public <T extends YamlNode> T asClass(final Class<T> clazz, final Node type)
              throws YamlReadingException, ClassCastException {
            if (clazz == Scalar.class) {
              return clazz.cast(this);
            }
            throw new ClassCastException();
          }

          @Override
          public int compareTo(final YamlNode other) {
            if (other == null) {
              return 1;
            } else if (other instanceof final Scalar otherScalar) {
              final String otherValue = otherScalar.value();
              if (otherValue == null) {
                return 0;
              } else {
                return -1;
              }
            }
            return -1;
          }
        };

    final ConfigurationNode node =
        YamlNodeAdapter.adapt(
            Yaml.createYamlMappingBuilder()
                .add("no value", nullValueScalar)
                .add("null", nullValueScalar)
                .add("true", "true")
                .add("false", "false")
                .add("integer", "10")
                .add("decimal", "10.0")
                .add("string", "some string")
                .add("list", Yaml.createYamlSequenceBuilder().add("10").add("some string").build())
                .add("map", Yaml.createYamlMappingBuilder().add("key", "value").build())
                .build());

    assertTrue(nodeIsMix(node));
  }
}
