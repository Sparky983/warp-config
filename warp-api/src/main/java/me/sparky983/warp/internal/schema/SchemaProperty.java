package me.sparky983.warp.internal.schema;

import me.sparky983.warp.internal.ParameterizedType;

/** A property in a schema. */
public interface SchemaProperty {
  /**
   * Returns the path of this property.
   *
   * @return the path.
   */
  String path();

  /**
   * Returns the type of this property.
   *
   * @return the path.
   */
  ParameterizedType<?> type();
}
