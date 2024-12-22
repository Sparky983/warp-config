package me.sparky983.warp.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Deserializer;

/** The default implementation of {@link DeserializerRegistry}. */
final class DefaultDeserializerRegistry implements DeserializerRegistry {
  private final Map<Class<?>, Deserializer<?>> deserializers;
  private final List<DeserializerFactory> factories;

  private DefaultDeserializerRegistry(
      final Map<Class<?>, Deserializer<?>> deserializers,
      final List<DeserializerFactory> factories) {
    this.deserializers = new HashMap<>(deserializers);
    this.factories = new ArrayList<>(factories);
  }

  /**
   * Gets the deserializer for the given type.
   *
   * @param type the type
   * @return an optional containing the deserializer if one was found, otherwise an {@linkplain
   *     Optional#empty() empty optional}
   * @param <T> the type
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<Deserializer<? extends T>> get(final ParameterizedType<T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    {
      final Deserializer<? extends T> deserializer =
          (Deserializer<? extends T>) deserializers.get(type.rawType());
      if (deserializer != null) {
        return Optional.of(deserializer);
      }
    }

    for (final DeserializerFactory factory : factories) {
      final Optional<Deserializer<? extends T>> deserializer;
      try {
        deserializer = factory.create(DefaultDeserializerRegistry.this, type);
      } catch (final IllegalStateException e) {
        throw e;
      } catch (final Exception e) {
        throw new IllegalStateException(
            "Exception occurred while creating deserializer for type " + type, e);
      }

      if (deserializer == null) {
        throw new IllegalStateException("Factory returned null for type " + type);
      }

      if (deserializer.isEmpty()) {
        continue;
      }
      return deserializer;
    }
    return Optional.empty();
  }

  /** The default implementation of {@link Builder}. */
  static final class DefaultBuilder implements Builder {
    private final Map<Class<?>, Deserializer<?>> deserializers = new HashMap<>();
    private final List<DeserializerFactory> factories = new ArrayList<>();

    @Override
    public <T> Builder deserializer(
        final Class<T> type, final Deserializer<? extends T> deserializer) {
      Objects.requireNonNull(type, "type cannot be null");
      Objects.requireNonNull(deserializer, "deserializer cannot be null");

      deserializers.put(type, deserializer);
      return this;
    }

    @Override
    public Builder factory(final DeserializerFactory factory) {
      Objects.requireNonNull(factory, "factory cannot be null");

      factories.add(factory);
      return this;
    }

    @Override
    public DeserializerRegistry build() {
      return new DefaultDeserializerRegistry(deserializers, factories);
    }
  }
}
