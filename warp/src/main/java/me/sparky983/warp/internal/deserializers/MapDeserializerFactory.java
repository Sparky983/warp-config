package me.sparky983.warp.internal.deserializers;

import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;

/** A {@link DeserializerFactory} for the {@link Map} type. */
public final class MapDeserializerFactory implements DeserializerFactory {
  /** Constructs a {@code MapDeserializerFactory}. */
  public MapDeserializerFactory() {}

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<Deserializer<? extends T>> create(
      DeserializerRegistry registry, ParameterizedType<? extends T> type) {
    if (type.rawType() != Map.class) {
      return Optional.empty();
    }

    if (type.isRaw()) {
      throw new IllegalStateException("Map must have two type arguments");
    }
    final ParameterizedType<?> keyType = type.typeArguments().get(0);
    final ParameterizedType<?> valueType = type.typeArguments().get(1);
    final Deserializer<?> keyDeserializer =
        registry
            .get(keyType)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Deserializer for the keys of " + type + " not found"));
    final Deserializer<?> valueDeserializer =
        registry
            .get(valueType)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Deserializer for the values of " + type + " not found"));
    return Optional.of((Deserializer<T>) Deserializers.map(keyDeserializer, valueDeserializer));
  }
}
