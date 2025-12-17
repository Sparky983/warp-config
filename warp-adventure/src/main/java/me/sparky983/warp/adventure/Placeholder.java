package me.sparky983.warp.adventure;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.text.ChoiceFormat;
import java.time.temporal.TemporalAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;

/**
 * Marks the annotated parameter as a {@linkplain
 * net.kyori.adventure.text.minimessage.tag.resolver.Placeholder#component(String, ComponentLike)
 * component placeholder} for the {@linkplain ComponentDeserializer#miniMessage(MiniMessage) mini
 * message deserializer}.
 *
 * <p>All placeholders arguments resolve tags with the given {@linkplain #value() name} to the
 * provided argument by the caller, usually formatted in some way.
 *
 * <p>{@link ComponentDeserializer#miniMessage(MiniMessage)} allows only the following types which
 * resolve the placeholder tag into text components using according to {@linkplain Component
 * Adventure's text component factory methods}:
 *
 * <ul>
 *   <li>{@code int}
 *   <li>{@code long}
 *   <li>{@code float}
 *   <li>{@code double}
 *   <li>{@code char}
 *   <li>{@code boolean}
 *   <li>{@link String} - allows {@code null}
 *   <li>any type assignable to {@link ComponentLike} - allows {@code null}
 * </ul>
 *
 * @see net.kyori.adventure.text.minimessage.tag.resolver.Placeholder#component(String,
 *     ComponentLike)
 * @since 0.2
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Placeholder {
  /**
   * The placeholder name. Specifies name of tags to resolve.
   *
   * @return the placeholder name
   * @since 0.2
   */
  String value();

  /**
   * Marks the annotated parameter as a choice placeholder.
   *
   * <p>Choice placeholders allow for {@linkplain Formatter#booleanChoice(String, boolean) boolean
   * choice placeholders} if the annotated parameter is {@code boolean} or {@link
   * Formatter#choice(String, Number) number choice placeholders} if the parameter is {@code int},
   * {@code long}, {@code float} or {@code double}.
   *
   * <p>Boolean choice placeholder tags have two arguments: what to resolve {@code true} to and what
   * to resolve {@code false} to
   *
   * <p>Number choice placeholder tags take in an argument of the format specified by {@link
   * ChoiceFormat}.
   *
   * @see Placeholder
   * @see Formatter#booleanChoice(String, boolean)
   * @see Formatter#choice(String, Number)
   * @see ChoiceFormat
   * @since 0.2
   */
  @Documented
  @Retention(RUNTIME)
  @Target(PARAMETER)
  @interface Choice {
    /**
     * The placeholder name. Specifies name of tags to resolve.
     *
     * @return the placeholder name
     * @since 0.2
     */
    String value();
  }

  /**
   * Marks the annotated method as a format placeholder.
   *
   * <p>The two types of format placeholders:
   *
   * <ul>
   *   <li>{@linkplain Formatter#number(String, Number) number formatters} for {@code int}, {@code
   *       long}, {@code float} and {@code double}
   *   <li>{@linkplain Formatter#date(String, TemporalAccessor) date formatters} for types
   *       assignable to {@link TemporalAccessor}
   * </ul>
   *
   * @see Placeholder
   * @see Formatter#number(String, Number)
   * @see Formatter#date(String, TemporalAccessor)
   * @since 0.2
   */
  @Documented
  @Retention(RUNTIME)
  @Target(PARAMETER)
  @interface Format {
    /**
     * The placeholder name. Specifies name of tags to resolve.
     *
     * @return the placeholder name
     * @since 0.2
     */
    String value();
  }

  /**
   * Marks the annotated parameter as a {@linkplain
   * net.kyori.adventure.text.minimessage.tag.resolver.Placeholder#parsed(String, String) parsed
   * placeholder}.
   *
   * <p>The tag is replaced by the argument provided by the caller and directly parsed by mini
   * message.
   *
   * <p>The annotated parameter must be a {@link String} parameter.
   *
   * @see Placeholder
   * @see net.kyori.adventure.text.minimessage.tag.resolver.Placeholder#parsed(String, String)
   * @since 0.2
   */
  @Documented
  @Retention(RUNTIME)
  @Target(PARAMETER)
  @interface Parsed {
    /**
     * The placeholder name. Specifies name of tags to resolve.
     *
     * @return the placeholder name
     * @since 0.2
     */
    String value();
  }
}
