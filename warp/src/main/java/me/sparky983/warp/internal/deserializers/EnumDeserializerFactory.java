package me.sparky983.warp.internal.deserializers;

import java.util.Optional;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;

/** A {@link DeserializerFactory} for the {@link Enum} type. */
public final class EnumDeserializerFactory implements DeserializerFactory {
  /** Constructs a {@code EnumDeserializerFactory}. */
  public EnumDeserializerFactory() {}

  @Override
  public <T> Optional<Deserializer<? extends T>> create(
      final DeserializerRegistry registry, final ParameterizedType<? extends T> type) {
    final Class<? extends T> rawType = type.rawType();
    if (!rawType.isEnum()) {
      return Optional.empty();
    }
    @SuppressWarnings({"unchecked", "rawtypes"})
    final Deserializer<T> deserializer = Deserializers.enumeration((Class) rawType);
    return Optional.of(deserializer);
  }
}
