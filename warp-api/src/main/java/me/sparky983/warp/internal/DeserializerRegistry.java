package me.sparky983.warp.internal;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationValue;
import org.jspecify.annotations.NullMarked;

/** A registry of {@link Deserializer Deserializers}. */
@NullMarked
public final class DeserializerRegistry {
  private final Map<DeserializerQualifier, Deserializer<?, ?>> deserializers = new HashMap<>();

  private DeserializerRegistry() {}

  static DeserializerRegistry create() {
    return new DeserializerRegistry();
  }

  /**
   * Registers the given deserializer.
   *
   * @param valueType the type of the serialized value; {@code ConfigurationValue.class} registers
   *     for all
   * @param deserializedType the type to deserialize to
   * @param deserializer the serializer
   * @return this registry
   * @param <F> the type of the serialized value
   * @param <T> the type to deserialize to
   * @throws NullPointerException if the value type, deserialized type or the deserializer are
   *     {@code null}.
   */
  public <F extends ConfigurationValue, T> DeserializerRegistry register(
      final Class<F> valueType,
      final Class<T> deserializedType,
      final Deserializer<? super F, ? extends T> deserializer) {
    Objects.requireNonNull(deserializer, "deserializer cannot be null");

    deserializers.put(new DeserializerQualifier(valueType, deserializedType), deserializer);
    return this;
  }

  public <F extends ConfigurationValue, T> Optional<T> deserialize(
      final F serialized, final Class<T> type, final Type genericType) {
    Objects.requireNonNull(serialized, "serialized cannot be null");
    Objects.requireNonNull(type, "type cannot be null");
    Objects.requireNonNull(genericType, "genericType cannot be null");

    final Class<? extends ConfigurationValue> serializedType;
    if (serialized instanceof ConfigurationValue.Primitive) {
      serializedType = ConfigurationValue.Primitive.class;
    } else if (serialized instanceof ConfigurationValue.List) {
      serializedType = ConfigurationValue.List.class;
    } else if (serialized instanceof ConfigurationValue.Map) {
      serializedType = ConfigurationValue.Map.class;
    } else {
      throw new AssertionError("Unexpected configuration value");
    }

    return get(serializedType, type)
        .or(() -> get(ConfigurationValue.class, type))
        .flatMap((deserializer) -> deserializer.deserialize(genericType, serialized));
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <F extends ConfigurationValue, T> Optional<Deserializer<F, T>> get(
      final Class<? extends ConfigurationValue> serializedType, final Class<T> type) {
    final var deserializer = deserializers.get(new DeserializerQualifier(serializedType, type));
    return Optional.ofNullable((Deserializer) deserializer);
  }

  private record DeserializerQualifier(
      Class<? extends ConfigurationValue> valueType, Class<?> deserializedType) {
    public DeserializerQualifier {
      Objects.requireNonNull(valueType, "valueType cannot be null");
      Objects.requireNonNull(deserializedType, "deserializedType cannot be null");
    }
  }
}
