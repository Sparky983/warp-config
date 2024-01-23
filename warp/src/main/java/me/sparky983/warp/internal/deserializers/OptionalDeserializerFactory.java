package me.sparky983.warp.internal.deserializers;

import java.util.Optional;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;

/** A {@link DeserializerFactory} for the {@link Optional} type. */
public final class OptionalDeserializerFactory implements DeserializerFactory {
  /** Constructs a {@code OptionalDeserializerFactory}. */
  public OptionalDeserializerFactory() {}

  @SuppressWarnings({"unchecked"})
  @Override
  public <T> Optional<Deserializer<? extends T>> create(
      final DeserializerRegistry registry, final ParameterizedType<? extends T> type) {
    if (type.rawType() != Optional.class) {
      return Optional.empty();
    }

    if (type.isRaw()) {
      throw new IllegalStateException("Optional must have a type argument");
    }
    final ParameterizedType<?> valueType = type.typeArguments().get(0);
    final Deserializer<?> deserializer =
        registry
            .get(valueType)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Deserializer for the value of " + type + " not found"));
    return Optional.of((Deserializer<T>) Deserializers.optional(deserializer));
  }
}
