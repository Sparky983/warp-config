package me.sparky983.warp.internal;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * A runtime representation of a type that is optionally parameterized.
 *
 * @param <T> the type
 */
public final class ParameterizedType<T> {
  // Note: this class cannot represent arrays with generic component types

  private final Class<T> rawType;
  private final List<ParameterizedType<?>> typeArguments;

  private ParameterizedType(
      final Class<T> rawType, final List<ParameterizedType<?>> typeArguments) {
    Objects.requireNonNull(rawType, "rawType cannot be null");

    this.typeArguments = List.copyOf(typeArguments);

    if (!typeArguments.isEmpty() && rawType.getTypeParameters().length != typeArguments.size()) {
      throw new IllegalArgumentException(
              rawType + " declares " + rawType.getTypeParameters().length + " type parameters but found " + typeArguments.size() + " type arguments");
    }

    this.rawType = rawType;
  }

  /**
   * Returns {@code ParameterizedType} with the given raw type and no type arguments.
   *
   * @param rawType a class representing the raw type
   * @return the parameterized type
   * @param <T> the raw type
   * @throws NullPointerException if the class is {@code null}.
   */
  public static <T> ParameterizedType<T> of(final Class<T> rawType) {
    return new ParameterizedType<>(rawType, List.of());
  }

  /**
   * Returns a {@code ParameterizedType} with the given raw type and type arguments.
   *
   * @param rawType a class representing the raw type
   * @param typeArguments the type arguments; changes to this array will not be reflected in the
   *     resulting parameterized type's type arguments
   * @return the parameterized type
   * @param <T> the type
   * @throws IllegalArgumentException if the number of type arguments is not 0 (raw type) and the
   *     number of type arguments don't match the amount of type parameters declared by the class.
   * @throws NullPointerException if the class, the type arguments or a type is {@code null}.
   */
  public static <T> ParameterizedType<T> of(
      final Class<T> rawType, final ParameterizedType<?>... typeArguments) {
    return new ParameterizedType<>(rawType, List.of(typeArguments));
  }

  /**
   * Returns a {@code ParameterizedType} with the given raw type and type arguments.
   *
   * @param rawType a class representing the raw type
   * @param typeArguments the type arguments; changes to this array will not be reflected in the
   *     resulting parameterized type's type arguments
   * @return the parameterized type
   * @param <T> the type
   * @throws IllegalArgumentException if the number of type arguments is not 0 (raw type) and the
   *     number of type arguments don't match the amount of type parameters declared by the class.
   * @throws NullPointerException if the class, the type arguments or a type is {@code null}.
   */
  public static <T> ParameterizedType<T> of(
      final Class<T> rawType, final Class<?>... typeArguments) {
    return new ParameterizedType<>(
        rawType,
        Stream.of(typeArguments)
            .map(Objects::requireNonNull)
            .<ParameterizedType<?>>map(ParameterizedType::of)
            .toList());
  }

  /**
   * Returns a {@code ParameterizedType} for the given {@link Type}.
   *
   * @param type the type
   * @return the parameterized type
   * @throws IllegalArgumentException if the given type either is or references (directly or
   *     indirectly) a type that is not an instance of one of the following:
   *     <ul>
   *       <li>{@link Class}
   *       <li>{@link java.lang.reflect.ParameterizedType}
   *       <li>{@link WildcardType}
   *       <li>{@link GenericArrayType}
   *     </ul>
   *
   * @throws NullPointerException if the class, the type arguments array or any of the type
   *     arguments are {@code null}.
   */
  public static ParameterizedType<?> of(final Type type) {
    if (type instanceof final Class<?> cls) {
      return of(cls);
    } else if (type instanceof final java.lang.reflect.ParameterizedType parameterizedType) {
      return new ParameterizedType<>(
          // This cast is safe actually safe - https://bugs.openjdk.org/browse/JDK-6255169
          (Class<?>) parameterizedType.getRawType(),
          Stream.of(parameterizedType.getActualTypeArguments())
              .<ParameterizedType<?>>map(ParameterizedType::of)
              .toList());
    } else if (type instanceof final WildcardType wildcardType) {
      // Currently Java only supports a single bound
      return of(wildcardType.getUpperBounds()[0]);
    } else if (type instanceof final GenericArrayType genericArrayType) {
      return of(of(genericArrayType.getGenericComponentType()).rawType().arrayType());
    } else if (type instanceof TypeVariable<?>) {
      throw new IllegalArgumentException("Type variables are not allowed in ParameterizedType");
    } else {
      throw new IllegalArgumentException("Unexpected type %s" + type.getTypeName());
    }
  }

  /**
   * Returns the raw type.
   *
   * @return the raw type
   */
  public Class<T> rawType() {
    return rawType;
  }

  /**
   * Returns the type arguments.
   *
   * @return the type arguments
   */
  public List<ParameterizedType<?>> typeArguments() {
    return typeArguments;
  }

  /**
   * Returns the raw types of each type arguments.
   *
   * @return the type arguments
   */
  public List<Class<?>> rawTypeArguments() {
    return typeArguments.stream().<Class<?>>map(ParameterizedType::rawType).toList();
  }

  /**
   * Checks whether this parameterized type has type arguments.
   *
   * @return {@code true} if this parameterized type has type arguments, otherwise {@code false}
   */
  public boolean isParameterized() {
    return !isRaw();
  }

  /**
   * Checks whether this parameterized type has no type arguments.
   *
   * @return {@code true} if this parameterized type has no type arguments, otherwise {@code false}
   */
  public boolean isRaw() {
    return typeArguments.isEmpty();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) {
      return true;
    }

    if (!(other instanceof final ParameterizedType<?> parameterizedType)) {
      return false;
    }

    return parameterizedType.rawType().equals(rawType)
        && parameterizedType.typeArguments().equals(typeArguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rawType, typeArguments);
  }

  @Override
  public String toString() {
    if (isRaw()) {
      return rawType.getTypeName();
    }
    final StringJoiner joiner = new StringJoiner(", ", "<", ">");
    for (final ParameterizedType<?> typeArgument : typeArguments) {
      joiner.add(typeArgument.toString());
    }
    return rawType().getTypeName() + joiner;
  }
}
