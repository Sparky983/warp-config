package me.sparky983.warp.internal;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;
import org.jspecify.annotations.NullMarked;

/**
 * A runtime representation of a type that is optionally parameterized.
 *
 * @param <T> the type
 */
@NullMarked
public final class ParameterizedType<T> {
   // Note: this class cannot represent generic arrays. This shouldn't be needed though.
  
  private final Class<T> rawType;
  private final List<ParameterizedType<?>> typeArguments;

  private ParameterizedType(
      final Class<T> rawType, final List<ParameterizedType<?>> typeArguments) {
    Objects.requireNonNull(rawType, "rawType cannot be null");

    this.rawType = rawType;
    this.typeArguments = List.copyOf(typeArguments);
  }

  public static <T> ParameterizedType<T> of(final Class<T> rawType) {
    return new ParameterizedType<>(rawType, List.of());
  }

  public static <T> ParameterizedType<T> of(
      final Class<T> rawType, final ParameterizedType<?>... typeArguments) {
    return new ParameterizedType<>(rawType, List.of(typeArguments));
  }

  public static <T> ParameterizedType<T> of(
      final Class<T> rawType, final Class<?>... typeArguments) {
    return new ParameterizedType<>(
        rawType,
        Stream.of(typeArguments)
            .map(Objects::requireNonNull)
            .<ParameterizedType<?>>map(ParameterizedType::of)
            .toList());
  }

  public static ParameterizedType<?> of(final Type type) {
    if (type instanceof Class<?> cls) {
      return of(cls);
    } else if (type instanceof java.lang.reflect.ParameterizedType parameterizedType) {
      return new ParameterizedType<>(
          // this is actually safe - https://bugs.openjdk.org/browse/JDK-6255169
          (Class<?>) parameterizedType.getRawType(),
          Stream.of(parameterizedType.getActualTypeArguments())
              .<ParameterizedType<?>>map(ParameterizedType::of)
              .toList());
    } else if (type instanceof WildcardType wildcardType) {
      // currently Java only supports a single bound
      return of(wildcardType.getUpperBounds()[0]);
    } else if (type instanceof GenericArrayType genericArrayType) {
      return of(of(genericArrayType.getGenericComponentType()).rawType().arrayType());
    } else if (type instanceof TypeVariable<?>) {
      throw new IllegalArgumentException("Type variables are not allowed in ParameterizedType");
    } else {
      throw new IllegalArgumentException(String.format("Unexpected type %s", type.getTypeName()));
    }
  }

  public Class<T> rawType() {
    return rawType;
  }

  public List<ParameterizedType<?>> typeArguments() {
    return typeArguments;
  }

  public List<Class<?>> rawTypeArguments() {
    return typeArguments.stream().<Class<?>>map(ParameterizedType::rawType).toList();
  }

  public boolean isParameterized() {
    return !isRaw();
  }

  public boolean isRaw() {
    return typeArguments.isEmpty();
  }

  @Override
  public String toString() {
    if (isRaw()) {
      return rawType.getTypeName();
    }
    final var joiner = new StringJoiner(", ", "<", ">");
    for (final var typeArgument : typeArguments) {
      joiner.add(typeArgument.toString());
    }
    return rawType().getTypeName() + joiner;
  }
}
