package me.sparky983.warp.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;
import org.intellij.lang.annotations.Pattern;
import org.jspecify.annotations.NullMarked;

/**
 * Marks a method of an {@link Configuration @Configuration} class as a property.
 *
 * <p>Property methods must:
 * <ul>
 *   <li>Be {@code public}
 *   <li>Not be {@code static}
 *   <li>Not be generic
 *   <li>Have no additional parameters (an explicit receiver parameter is allowed)
 * </ul>
 *
 * <p>Properties can be declared optional by using an {@link Optional} return type.
 *
 * <p>Implementations must handle at least the following return types:
 *
 * <ul>
 *   <li>{@code byte}, {@code short}, {@code int}, {@code long}
 *   <li>{@code float}, {@code double}
 *   <li>{@code boolean}
 *   <li>{@code char}
 *   <li>{@link String}
 * </ul>
 *
 * @since 0.1
 */
@NullMarked
@Retention(RUNTIME)
@Target(METHOD)
public @interface Property {
  /**
   * The property's path, delimited by ".".
   *
   * <p>The path can only consist of alphanumeric characters, "-" and "_".
   *
   * @return the path
   * @since 0.1
   */
  @Pattern("[-\\w]+(\\.[-\\w]+)*")
  String value();
}
