package me.sparky983.warp.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a class as a configuration class.
 *
 * <p>Configuration classes must:
 *
 * <ul>
 *   <li>Be an {@code interface}
 *   <li>Declare at least one {@link Property @Property} method
 * </ul>
 *
 * @since 0.1
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Configuration {}
