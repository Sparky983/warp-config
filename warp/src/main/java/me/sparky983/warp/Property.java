package me.sparky983.warp;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.intellij.lang.annotations.Pattern;

/**
 * Marks the annotated method as a property method.
 *
 * <h2>Requirements</h2>
 *
 * <p>The following is a list of requirements for a property class:
 *
 * <ul>
 *   <li>A property method must be {@code public}
 *   <li>A property method must not be {@code static}
 *   <li>A property method must not be generic
 *   <li>A property method must not declare any parameters (an explicit receiver parameter is
 *       allowed)
 *   <li>A property method must be a member of a {@linkplain Configuration configuration class}
 * </ul>
 *
 * <h2>Implementation Requirements</h2>
 *
 * Implementations of methods property methods must:
 *
 * <ul>
 *   <li>Never return {@code null}
 *   <li>Never throw an exception
 * </ul>
 *
 * @since 0.1
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Property {
  /**
   * The property's <a href="#path">path</a>.
   *
   * <h4>Path</h4>
   *
   * A path is a sequence of keys, each consisting of only alphanumeric character, hyphens ("-") and
   * underscores ("_"), delimited by the "." character.
   *
   * @return the path
   * @since 0.1
   */
  @Pattern("[-\\w]+(\\.[-\\w]+)*")
  String value();
}
