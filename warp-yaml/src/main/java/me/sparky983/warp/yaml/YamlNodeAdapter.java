package me.sparky983.warp.yaml;

import com.amihaiemil.eoyaml.Scalar;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;

/**
 * A utility class that converts {@link YamlNode YamlNodes} to {@link ConfigurationNode
 * ConfigurationNodes}.
 */
final class YamlNodeAdapter {
  private YamlNodeAdapter() {}

  static ConfigurationNode adapt(final YamlNode node) {
    Objects.requireNonNull(node, "node cannot be null");

    if (node instanceof final YamlMapping mapping) {
      return adapt(mapping);
    } else if (node instanceof final YamlSequence sequence) {
      return adapt(sequence);
    } else if (node instanceof final Scalar scalar) {
      return adapt(scalar);
    } else {
      throw new IllegalArgumentException("Unknown node type: " + node.getClass());
    }
  }

  private static ConfigurationNode adapt(final YamlMapping mapping) {
    Objects.requireNonNull(mapping, "mapping cannot be null");

    final Set<YamlNode> keys = mapping.keys();
    final Map<String, ConfigurationNode> map = new HashMap<>(keys.size());

    for (final YamlNode key : keys) {
      if (key instanceof final Scalar scalar) {
        final String value = scalar.value();
        if (value != null) {
          map.put(value, adapt(mapping.value(key)));
        }
        continue;
      }
      throw new IllegalArgumentException("Invalid key: " + key);
    }
    return ConfigurationNode.map(map);
  }

  private static ConfigurationNode adapt(final YamlSequence sequence) {
    Objects.requireNonNull(sequence, "sequence cannot be null");

    return ConfigurationNode.list(sequence.values().stream().map(YamlNodeAdapter::adapt).toList());
  }

  private static ConfigurationNode adapt(final Scalar scalar) {
    Objects.requireNonNull(scalar, "scalar cannot be null");

    final String value = scalar.value();

    // no value can't be differentiated from "null" so is convertible to a string
    return new YamlScalarConfigurationNode(value == null ? "null" : value);
  }

  record YamlScalarConfigurationNode(String value) implements ConfigurationNode {
    @Override
    public String asString() {
      return value;
    }

    @Override
    public double asDecimal() throws DeserializationException {
      try {
        return Double.parseDouble(value);
      } catch (final NumberFormatException exception) {
        return ConfigurationNode.super.asDecimal();
      }
    }

    @Override
    public long asInteger() throws DeserializationException {
      try {
        return Long.parseLong(value);
      } catch (final NumberFormatException exception) {
        return ConfigurationNode.super.asInteger();
      }
    }

    @Override
    public boolean asBoolean() throws DeserializationException {
      return switch (value) {
        case "true" -> true;
        case "false" -> false;
        default -> ConfigurationNode.super.asBoolean();
      };
    }

    @Override
    public boolean isNil() {
      return value.equals("null");
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
