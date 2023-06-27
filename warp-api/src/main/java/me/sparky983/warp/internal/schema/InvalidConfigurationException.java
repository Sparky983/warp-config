package me.sparky983.warp.internal.schema;

import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/** Thrown by {@link ConfigurationSchema} if a configuration does not comply to the schema. */
@NullMarked
public final class InvalidConfigurationException extends Exception {
  private final Set<SchemaViolation> violations;

  /**
   * Constructs the exception.
   *
   * @param message the message
   * @param violations a set of all the violations; changes to this set will not be reflected in the
   *     set returned by {@link #schemaViolations()}
   * @throws NullPointerException if the violations set is {@code null} or one of the violations are
   *     {@code null}.
   */
  public InvalidConfigurationException(
      final @Nullable String message, Set<SchemaViolation> violations) {
    super(message);
    this.violations = Set.copyOf(violations);
  }

  /**
   * Returns an unmodifiable set of the violations
   *
   * @return the violations
   */
  public Set<SchemaViolation> schemaViolations() {
    return violations;
  }
}
