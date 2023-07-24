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
 *   <li>Be {@code public}
 *   <li>Not be {@code sealed}
 *   <li>Not be hidden
 *   <li>Not be generic
 *   <li>Declare only {@code private}, or {@code public abstract} methods (interface methods are
 *       implicitly {@code public abstract}) annotated with {@link Property @Property}.
 * </ul>
 *
 * @since 0.1
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Configuration {}
