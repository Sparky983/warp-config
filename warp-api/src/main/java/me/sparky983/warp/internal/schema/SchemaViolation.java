package me.sparky983.warp.internal.schema;

import java.util.Objects;
import org.jspecify.annotations.NullMarked;

/** Represents a violation of a constraint defined by a schema */
@NullMarked
public record SchemaViolation(String description) {
  /**
   * Constructs the schema violation.
   *
   * @param description a description of the violation
   * @throws NullPointerException if the description is {@code null}.
   */
  public SchemaViolation {
    Objects.requireNonNull(description, "description cannot be null");
  }
}
