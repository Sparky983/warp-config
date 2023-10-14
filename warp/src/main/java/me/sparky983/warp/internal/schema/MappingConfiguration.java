package me.sparky983.warp.internal.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.DefaultsRegistry;
import me.sparky983.warp.internal.DeserializerRegistry;
import org.jspecify.annotations.Nullable;

/** A configuration that maps its values to objects as they are put. */
public final class MappingConfiguration {
  /** A cached deserializer context (the context is empty). */
  private static final Deserializer.Context CONTEXT = new Deserializer.Context() {};

  private final DefaultsRegistry defaultsRegistry;
  private final DeserializerRegistry deserializerRegistry;

  private final Map<Schema.Property<?>, Renderer<?>> properties = new HashMap<>();

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
   * Sets the value associated with the given property to the serialized version of the given value.
   *
   * @param property the property
   * @param value the value
   * @return an {@link Optional} containing a {@link ConfigurationError} if there was an error, or
   *     an {@linkplain Optional#empty() empty optional}
   * @param <T> the type of the property
   * @throws NullPointerException if the property is {@code null}, or if the deserializer associated
   *     with the given property's type returns {@code null}.
   */
  public <T> Optional<ConfigurationError> put(
      final Schema.Property<T> property, final @Nullable ConfigurationNode value) {
    Objects.requireNonNull(property, "property cannot be null");

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

    boolean error = false;
    final List<ConfigurationError> errors = new ArrayList<>();

    final ConfigurationNode actualNode =
        value == null ? defaultsRegistry.get(property.type().rawType()).orElse(null) : value;

    if (actualNode == null) {
      error = true;
      errors.add(ConfigurationError.error("Must be set to a value"));
    } else {
      try {
        final Renderer<T> renderer = deserializer.deserialize(actualNode, CONTEXT);
        properties.putIfAbsent(property, renderer);
        if (renderer == null) {
          throw new NullPointerException("Deserializer returned null");
        }
      } catch (final DeserializationException e) {
        error = true;
        errors.addAll(e.errors());
      }
    }

    if (error) {
      return Optional.of(ConfigurationError.group(path, errors));
    }
    return Optional.empty();
  }

  /**
   * Gets the value associated with the given property.
   *
   * @param property the {@link Schema.Property}
   * @param context the renderer context
   * @return an optional containing the value if it is present, otherwise an {@linkplain
   *     Optional#empty() empty optional}
   * @param <T> the type of the property
   * @throws NullPointerException if the path is {@code null} or if the renderer returned by the
   *     deserializer associated with the given property's type returns {@code null}.
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> render(final Schema.Property<T> property, final Renderer.Context context) {
    Objects.requireNonNull(property, "property cannot be null");

    return Optional.ofNullable((Renderer<T>) properties.get(property))
        .map(
            (render) -> {
              final T t = render.render(context);
              if (t == null) {
                throw new NullPointerException("Renderer returned null");
              }
              return t;
            });
  }
}
