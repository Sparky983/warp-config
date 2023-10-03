package me.sparky983.warp.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.Deserializer;

/** The default implementation of {@link DeserializerRegistry}. */
final class DefaultDeserializerRegistry implements DeserializerRegistry {
  private final Map<Class<?>, DeserializerFactory<?>> deserializerFactories;

  private DefaultDeserializerRegistry(
      final Map<Class<?>, DeserializerFactory<?>> deserializerFactories) {
    this.deserializerFactories = new HashMap<>(deserializerFactories);
  }

  /**
   * Gets the deserializer for the given type.
   *
   * @param type the type
   * @return an optional containing the deserializer if one was found, otherwise an empty optional
   * @param <T> the type
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> Optional<Deserializer<T>> get(final ParameterizedType<? extends T> type) {
    Objects.requireNonNull(type, "type cannot be null");

    return Optional.ofNullable((DeserializerFactory) deserializerFactories.get(type.rawType()))
        .map(
            (factory) -> {
              final Deserializer<T> deserializer;
              try {
                deserializer =
                    (Deserializer<T>) factory.create(DefaultDeserializerRegistry.this, type);
              } catch (final IllegalStateException e) {
                throw e;
              } catch (final Exception e) {
                throw new IllegalStateException(
                    "Exception occurred while creating deserializer for type " + type, e);
              }
              if (deserializer == null) {
                throw new IllegalStateException(
                    factory + " deserialize factory returned null for type " + type);
              }
              return deserializer;
            });
  }

  static final class DefaultBuilder implements Builder {
    private final Map<Class<?>, DeserializerFactory<?>> deserializerFactories = new HashMap<>();

    @Override
    public <T> Builder deserializer(
        final Class<T> type, final Deserializer<? extends T> deserializer) {
      Objects.requireNonNull(deserializer, "deserializer cannot be null");

      this.factory(type, (registry, parameterizedType) -> deserializer);
      return this;
    }

    @Override
    public <T> Builder factory(
        final Class<? extends T> type, final DeserializerFactory<T> factory) {
      Objects.requireNonNull(type, "type cannot be null");
      Objects.requireNonNull(factory, "factory cannot be null");

      deserializerFactories.put(type, factory);
      return this;
    }

    @Override
    public DeserializerRegistry build() {
      return new DefaultDeserializerRegistry(deserializerFactories);
    }
  }
}
