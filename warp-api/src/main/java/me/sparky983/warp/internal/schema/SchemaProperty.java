package me.sparky983.warp.internal.schema;

import java.util.Optional;
import me.sparky983.warp.ConfigurationValue;

/** A property in a schema. */
public interface SchemaProperty {
  String path();

  Optional<ConfigurationValue> defaultValue();
}
