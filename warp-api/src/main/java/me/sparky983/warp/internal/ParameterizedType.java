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
  // Note: this class cannot represent generic arrays

  private final Class<T> rawType;
  private final List<ParameterizedType<?>> typeArguments;

  private ParameterizedType(
      final Class<T> rawType, final List<ParameterizedType<?>> typeArguments) {
    Objects.requireNonNull(rawType, "rawType cannot be null");

    if (typeArguments.size() != 0 && rawType.getTypeParameters().length != typeArguments.size()) {
      throw new IllegalArgumentException(
          String.format(
              "%s declares %s type parameters but found %s type arguments",
              rawType, rawType.getTypeParameters().length, typeArguments.size()));
    }

    this.rawType = rawType;
    this.typeArguments = List.copyOf(typeArguments);
  }

  /**
   * Creates a new raw parameterized type for the given class.
   *
   * @param rawType the class
   * @return the new parameterized type
   * @param <T> the type
   * @throws NullPointerException if the class is {@code null}.
   */
  public static <T> ParameterizedType<T> of(final Class<T> rawType) {
    return new ParameterizedType<>(rawType, List.of());
  }

  /**
   * Creates a new parameterized type for the given class with the given type arguments.
   *
   * @param rawType the class
   * @param typeArguments the type arguments; changes to this array will not be reflected in the
   *     resulting parameterized type's type arguments
   * @return the new parameterized type
   * @param <T> the type
   * @throws IllegalArgumentException if the number of type arguments is not 0 (raw type) and the
   *     number of type arguments don't match the amount of type parameters declared by the class.
   * @throws NullPointerException if the class, the type arguments array or any of the type
   *     arguments are {@code null}.
   */
  public static <T> ParameterizedType<T> of(
      final Class<T> rawType, final ParameterizedType<?>... typeArguments) {
    return new ParameterizedType<>(rawType, List.of(typeArguments));
  }

  /**
   * Creates a new parameterized type for the given class with the given type arguments.
   *
   * @param rawType the class
   * @param typeArguments the type arguments; changes to this array will not be reflected in the
   *     resulting parameterized type's type arguments
   * @return the new parameterized type
   * @param <T> the type
   * @throws IllegalArgumentException if the number of type arguments is not 0 (raw type) and the
   *     number of type arguments don't match the amount of type parameters declared by the class.
   * @throws NullPointerException if the class, the type arguments array or any of the type
   *     arguments are {@code null}.
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
   * Creates a new parameterized type for the type.
   *
   * @param type the type
   * @throws IllegalArgumentException if the given type included a type variable or if the type was
   *     unexpected
   * @throws NullPointerException if the class, the type arguments array or any of the type
   *     arguments are {@code null}.
   */
  public static ParameterizedType<?> of(final Type type) {
    if (type instanceof Class<?> cls) {
      return of(cls);
    } else if (type instanceof java.lang.reflect.ParameterizedType parameterizedType) {
      return new ParameterizedType<>(
          // This is actually safe - https://bugs.openjdk.org/browse/JDK-6255169
          (Class<?>) parameterizedType.getRawType(),
          Stream.of(parameterizedType.getActualTypeArguments())
              .<ParameterizedType<?>>map(ParameterizedType::of)
              .toList());
    } else if (type instanceof WildcardType wildcardType) {
      // Currently Java only supports a single bound
      return of(wildcardType.getUpperBounds()[0]);
    } else if (type instanceof GenericArrayType genericArrayType) {
      return of(of(genericArrayType.getGenericComponentType()).rawType().arrayType());
    } else if (type instanceof TypeVariable<?>) {
      throw new IllegalArgumentException("Type variables are not allowed in ParameterizedType");
    } else {
      throw new IllegalArgumentException(String.format("Unexpected type %s", type.getTypeName()));
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
   * Returns the type arguments as classes.
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
