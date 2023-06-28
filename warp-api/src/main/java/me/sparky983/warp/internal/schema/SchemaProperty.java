package me.sparky983.warp.internal.schema;

import me.sparky983.warp.internal.ParameterizedType;

/** A property in a schema. */
public interface SchemaProperty {
  String path();

  ParameterizedType<?> type();
}
