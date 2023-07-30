package me.sparky983.warp.internal.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializationException;
import me.sparky983.warp.internal.Deserializer;
import me.sparky983.warp.internal.DeserializerRegistry;

public final class MappingConfiguration {
  private final DefaultsRegistry defaultsRegistry;
  private final DeserializerRegistry deserializerRegistry;

  private final Map<String, Object> properties = new HashMap<>();

  public MappingConfiguration(
      final DefaultsRegistry defaultsRegistry, final DeserializerRegistry deserializerRegistry) {
    this.defaultsRegistry = defaultsRegistry;
    this.deserializerRegistry = deserializerRegistry;
  }

  public <T> Optional<ConfigurationError> put(
      final Schema.Property<T> property, final List<? extends ConfigurationNode> tempValues) {
    final String path = property.path();

    final Deserializer<T> deserializer =
        deserializerRegistry
            .get(property.type())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        String.format(
                            "Property \"%s\" required a deserializer of type %s, but none was found",
                            property.path(), property.type())));

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
        properties.putIfAbsent(path, deserializer.deserialize(value));
      } catch (final DeserializationException e) {
        errors.add(ConfigurationError.error(e.getMessage()));
      }
    }

    if (errors.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(ConfigurationError.group(path, errors));
  }

  public Object get(final String path) {
    return properties.get(path);
  }

  @Override
  public String toString() {
    return properties.toString();
  }
}
