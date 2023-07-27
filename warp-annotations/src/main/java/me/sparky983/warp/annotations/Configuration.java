package me.sparky983.warp.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks the annotated class as a configuration class.
 * <h2 id="requirements">Requirements</h2>
 *
 * <p>The following is a list of requirements for a configuration class:
 *
 * <ul>
 *   <li>A configuration class must be be {@code public}
 *   <li>A configuration class must be an {@code interface}
 *   <li>A configuration class must not be {@link Class#isHidden() hidden}
 *   <li>A configuration class must not be {@link Class#isSealed() sealed}
 *   <li>A configuration class must not be generic
 *   <li>All member methods of a configuration class must be one of the following:
 *       <ul>
 *           <li>A {@code private} instance method
 *           <li>A {@code static} method
 *           <li>A {@link Property property method}
 *       </ul>
 * </ul>
 *
 * @since 0.1
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Configuration {}
