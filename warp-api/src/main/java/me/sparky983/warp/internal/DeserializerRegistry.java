package me.sparky983.warp.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;
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
   * @param nodeType the type of the serialized node; {@code ConfigurationValue.class} registers for
   *     all
   * @param deserializedType the type to deserialize to
   * @param deserializer the serializer
   * @return this registry
   * @param <F> the type of the serialized value
   * @param <T> the type to deserialize to
   * @throws NullPointerException if the value type, deserialized type or the deserializer are
   *     {@code null}.
   */
  <F extends ConfigurationNode, T> DeserializerRegistry register(
      final Class<F> nodeType,
      final Class<T> deserializedType,
      final Deserializer<? super F, ? extends T> deserializer) {
    Objects.requireNonNull(deserializer, "deserializer cannot be null");

    deserializers.put(new DeserializerQualifier(nodeType, deserializedType), deserializer);
    return this;
  }

  public <F extends ConfigurationNode, T> Optional<T> deserialize(
      final F serialized, final ParameterizedType<T> type) {
    Objects.requireNonNull(serialized, "serialized cannot be null");
    Objects.requireNonNull(type, "type cannot be null");

    final Class<? extends ConfigurationNode> serializedType;
    if (serialized instanceof ConfigurationNode.Primitive) {
      serializedType = ConfigurationNode.Primitive.class;
    } else if (serialized instanceof ConfigurationNode.List) {
      serializedType = ConfigurationNode.List.class;
    } else if (serialized instanceof ConfigurationNode.Map) {
      serializedType = ConfigurationNode.Map.class;
    } else if (serialized instanceof ConfigurationNode.Nil) {
      serializedType = ConfigurationNode.Nil.class;
    } else {
      throw new AssertionError("Unexpected configuration value");
    }

    return get(serializedType, type.rawType())
        .or(() -> get(ConfigurationNode.class, type.rawType()))
        .flatMap((deserializer) -> deserializer.deserialize(type, serialized));
  }

  @SuppressWarnings("unchecked")
  private <F extends ConfigurationNode, T> Optional<Deserializer<F, T>> get(
      final Class<? extends ConfigurationNode> serializedType, final Class<T> type) {
    final var deserializer = deserializers.get(new DeserializerQualifier(serializedType, type));
    return Optional.ofNullable((Deserializer<F, T>) deserializer);
  }

  private record DeserializerQualifier(
      Class<? extends ConfigurationNode> nodeType, Class<?> deserializedType) {
    private DeserializerQualifier {
      Objects.requireNonNull(nodeType, "nodeType cannot be null");
      Objects.requireNonNull(deserializedType, "deserializedType cannot be null");
    }
  }
}
