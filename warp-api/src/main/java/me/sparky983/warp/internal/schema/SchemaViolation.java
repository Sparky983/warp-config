package me.sparky983.warp.internal.schema;

import java.util.Objects;
import me.sparky983.warp.ConfigurationError;
import org.jspecify.annotations.NullMarked;

/** A {@link ConfigurationError} caused by the violation of a constraint defined by a schema. */
record SchemaViolation(@Override String description) implements ConfigurationError {
  /**
   * Constructs the schema violation.
   *
   * @param description a description of the violation
   * @throws NullPointerException if the description is {@code null}.
   */
  SchemaViolation {
    Objects.requireNonNull(description, "description cannot be null");
  }
}
