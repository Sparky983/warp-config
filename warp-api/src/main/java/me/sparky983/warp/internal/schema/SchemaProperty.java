package me.sparky983.warp.internal.schema;

import java.lang.reflect.Type;
import java.util.Optional;
import me.sparky983.warp.ConfigurationValue;

/** A property in a schema. */
public interface SchemaProperty {
  String path();

  Class<?> rawType();

  Type genericType();

  // TODO(Sparky983): Optional properties
  boolean isOptional();

  // TODO(Sparky983): Default values
  Optional<ConfigurationValue> defaultValue();
}
