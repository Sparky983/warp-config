package me.sparky983.warp.internal.schema;

import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.ParameterizedType;

/** A property in a schema. */
public interface SchemaProperty {
  String path();

  ParameterizedType<?> type();

  // TODO(Sparky983): Optional properties
  boolean isOptional();

  // TODO(Sparky983): Default values
  Optional<ConfigurationNode> defaultValue();
}
