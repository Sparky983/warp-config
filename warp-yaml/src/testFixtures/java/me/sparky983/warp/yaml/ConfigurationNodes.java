package me.sparky983.warp.yaml;

import me.sparky983.warp.ConfigurationNode;

public final class ConfigurationNodes {
  public static final ConfigurationNode MIX = ConfigurationNode.map()
      .entry("no value", ConfigurationNode.nil())
      .entry("null", ConfigurationNode.nil())
      .entry("true", ConfigurationNode.bool(true))
      .entry("false", ConfigurationNode.bool(false))
      .entry("integer", ConfigurationNode.integer(10))
      .entry("decimal", ConfigurationNode.decimal(10.0))
      .entry("string", ConfigurationNode.string("some string"))
      .entry("list", ConfigurationNode.list(
          ConfigurationNode.integer(10),
          ConfigurationNode.string("some string")
      ))
      .entry("map", ConfigurationNode.map()
          .entry("key", ConfigurationNode.string("value"))
          .build())
      .build();

  private ConfigurationNodes() {}
}
