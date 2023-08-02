package me.sparky983.warp.internal.schema;

import java.util.*;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import me.sparky983.warp.internal.DeserializerRegistry;

/** A configuration that maps its values to objects as they are put. */
public final class MappingConfiguration {
  private final DefaultsRegistry defaultsRegistry;
  private final DeserializerRegistry deserializerRegistry;

  private final Map<Schema.Property<?>, Object> properties = new HashMap<>();

  /**
   * Constructs a {@code MappingConfiguration}.
   *
   * @param defaultsRegistry the defaults registry
   * @param deserializerRegistry the deserializer registry
   * @throws NullPointerException if the defaults registry or the deserializer registry are {@code
   *     null}.
   */
  public MappingConfiguration(
      final DefaultsRegistry defaultsRegistry, final DeserializerRegistry deserializerRegistry) {
    Objects.requireNonNull(defaultsRegistry, "defaultsRegistry cannot be null");
    Objects.requireNonNull(deserializerRegistry, "deserializerRegistry cannot be null");

    this.defaultsRegistry = defaultsRegistry;
    this.deserializerRegistry = deserializerRegistry;
  }

  /**
   * Serializes the given values and sets the value associated with the given property to the first
   * value.
   *
   * @param property the property
   * @param tempValues the values
   * @return an {@link Optional} containing a {@link ConfigurationError} if there was an error, or
   *     an empty optional
   * @param <T> the type of the property
   * @throws NullPointerException if the property or the values are {@code null}.
   */
  public <T> Optional<ConfigurationError> put(
      final Schema.Property<T> property, final List<? extends ConfigurationNode> tempValues) {
    Objects.requireNonNull(property, "property cannot be null");
    Objects.requireNonNull(tempValues, "tempValues cannot be null");

    final String path = property.path();

    final Deserializer<T> deserializer =
        deserializerRegistry
            .get(property.type())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Property \""
                            + property.path()
                            + "\" required a deserializer of type "
                            + property.type()
                            + ", but none was found"));

    final Set<ConfigurationError> errors = new HashSet<>();

    final List<? extends ConfigurationNode> values =
        tempValues.isEmpty()
            ? defaultsRegistry.get(property.type().rawType()).map(List::of).orElseGet(List::of)
            : tempValues;

    if (values.isEmpty()) {
      errors.add(ConfigurationError.error("Must be set to a value"));
    }

    for (final ConfigurationNode value : values) {
      try {
        // We still want to deserialize the value, even if it's already been set so we still get an
        // error message if it
        // couldn't be deserialized
        properties.putIfAbsent(property, deserializer.deserialize(value));
      } catch (final DeserializationException e) {
        errors.add(ConfigurationError.error(e.getMessage()));
      }
    }

    if (errors.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(ConfigurationError.group(path, errors));
  }

  /**
   * Gets the value associated with the given property.
   *
   * @param property the {@link Schema.Property}
   * @return an optional containing the value if it is present, otherwise an empty optional
   * @param <T> the type of the property
   * @throws NullPointerException if the path is {@code null}
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> get(final Schema.Property<T> property) {
    Objects.requireNonNull(property, "property cannot be null");

    return (Optional<T>) Optional.ofNullable(properties.get(property));
  }

  @Override
  public String toString() {
    final StringJoiner joiner = new StringJoiner(", ", "{", "}");
    properties.forEach((property, value) -> joiner.add(property.path() + "=" + value));
    return joiner.toString();
  }
}
