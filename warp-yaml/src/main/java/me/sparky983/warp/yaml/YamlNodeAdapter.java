package me.sparky983.warp.yaml;

import com.amihaiemil.eoyaml.Scalar;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import java.util.Objects;
import me.sparky983.warp.ConfigurationNode;
import org.jspecify.annotations.Nullable;

/**
 * A utility class that converts {@link YamlNode YamlNodes} to {@link ConfigurationNode
 * ConfigurationNodes}.
 */
final class YamlNodeAdapter {
  private YamlNodeAdapter() {}

  private static ConfigurationNode adapt(final @Nullable YamlNode node) {
    if (node == null) {
      return ConfigurationNode.nil();
    } else if (node instanceof final YamlMapping mapping) {
      return adapt(mapping);
    } else if (node instanceof final YamlSequence sequence) {
      return adapt(sequence);
    } else if (node instanceof final Scalar scalar) {
      return adapt(scalar);
    } else {
      throw new IllegalArgumentException("Unknown node type: " + node.getClass());
    }
  }

  static ConfigurationNode.Map adapt(final YamlMapping mapping) {
    Objects.requireNonNull(mapping, "mapping cannot be null");

    final ConfigurationNode.Map.Builder builder = ConfigurationNode.map();
    for (final YamlNode key : mapping.keys()) {
      if (key instanceof final Scalar scalar) {
        builder.entry(scalar.value(), adapt(mapping.value(key)));
      } else {
        throw new IllegalArgumentException("Unknown key type: " + key.getClass());
      }
    }
    return builder.build();
  }

  private static ConfigurationNode.List adapt(final YamlSequence sequence) {
    Objects.requireNonNull(sequence, "sequence cannot be null");

    return ConfigurationNode.list(sequence.values().stream().map(YamlNodeAdapter::adapt).toList());
  }

  private static ConfigurationNode adapt(final Scalar scalar) {
    Objects.requireNonNull(scalar, "scalar cannot be null");

    final String value = scalar.value();

    if (value == null || value.equals("null")) {
      return ConfigurationNode.nil();
    }

    if (value.equals("true")) {
      return ConfigurationNode.bool(true);
    }

    if (value.equals("false")) {
      return ConfigurationNode.bool(false);
    }

    try {
      return ConfigurationNode.integer(Long.parseLong(value));
    } catch (final NumberFormatException ignored) {
    }

    try {
      return ConfigurationNode.decimal(Double.parseDouble(value));
    } catch (final NumberFormatException ignored) {
    }

    return ConfigurationNode.string(value);
  }
}
