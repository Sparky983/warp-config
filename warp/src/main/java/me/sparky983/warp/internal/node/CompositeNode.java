package me.sparky983.warp.internal.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;

public final class CompositeNode implements ConfigurationNode {
  private final ConfigurationNode[] nodes;

  public CompositeNode(final List<ConfigurationNode> nodes) {
    this.nodes = nodes.toArray(ConfigurationNode[]::new);
  }

  @Override
  public String asString() throws DeserializationException {
    for (final ConfigurationNode node : nodes) {
      try {
        return node.asString();
      } catch (final DeserializationException e) {
        // continue
      }
    }
    return ConfigurationNode.super.asString();
  }

  @Override
  public double asDecimal() throws DeserializationException {
    for (final ConfigurationNode node : nodes) {
      try {
        return node.asDecimal();
      } catch (final DeserializationException e) {
        // continue
      }
    }
    return ConfigurationNode.super.asDecimal();
  }

  @Override
  public long asInteger() throws DeserializationException {
    for (final ConfigurationNode node : nodes) {
      try {
        return node.asInteger();
      } catch (final DeserializationException e) {
        // continue
      }
    }
    return ConfigurationNode.super.asInteger();
  }

  @Override
  public boolean asBoolean() throws DeserializationException {
    for (final ConfigurationNode node : nodes) {
      try {
        return node.asBoolean();
      } catch (final DeserializationException e) {
        // continue
      }
    }
    return ConfigurationNode.super.asBoolean();
  }

  @Override
  public boolean isNil() {
    for (final ConfigurationNode node : nodes) {
      if (node.isNil()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<ConfigurationNode> asList() throws DeserializationException {
    for (final ConfigurationNode node : nodes) {
      try {
        return node.asList();
      } catch (final DeserializationException e) {
        // continue
      }
    }
    return ConfigurationNode.super.asList();
  }

  @Override
  public Map<String, ConfigurationNode> asMap() throws DeserializationException {
    boolean containsMap = false;
    final Map<String, List<ConfigurationNode>> flattened = new HashMap<>();

    for (final ConfigurationNode node : nodes) {
      try {
        node.asMap().forEach((key, value) -> {
          flattened.computeIfAbsent(key, (k) -> new ArrayList<>()).add(value);
        });
        containsMap = true;
      } catch (final DeserializationException e) {
        // continue
      }
    }
    if (containsMap) {
      final Map<String, ConfigurationNode> merged = new HashMap<>();
      flattened.forEach((key, values) -> {
        merged.putIfAbsent(key, new CompositeNode(values));
      });
      return merged;
    } else {
      return ConfigurationNode.super.asMap();
    }
  }
}
