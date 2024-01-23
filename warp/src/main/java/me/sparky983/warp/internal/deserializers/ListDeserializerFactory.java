package me.sparky983.warp.internal.deserializers;

import java.util.List;
import java.util.Optional;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;

/** A {@link DeserializerFactory} for the {@link List} type. */
public final class ListDeserializerFactory implements DeserializerFactory {
  /** Constructs a {@code ListDeserializerFactory}. */
  public ListDeserializerFactory() {}

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<Deserializer<? extends T>> create(
      DeserializerRegistry registry, ParameterizedType<? extends T> type) {
    if (type.rawType() != List.class) {
      return Optional.empty();
    }

    if (type.isRaw()) {
      throw new IllegalStateException("List must have a type argument");
    }
    final ParameterizedType<?> valueType = type.typeArguments().get(0);
    final Deserializer<?> deserializer =
        registry
            .get(valueType)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Deserializer for the elements of " + type + " not found"));
    return Optional.of((Deserializer<T>) Deserializers.list(deserializer));
  }
}
