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
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;

/**
 * Marks the annotated parameter as a {@linkplain
 * net.kyori.adventure.text.minimessage.tag.resolver.Placeholder#component(String, ComponentLike)
 * component placeholder} for the {@linkplain ComponentDeserializer#miniMessage(MiniMessage) mini
 * message deserializer}.
 *
 * <p>All placeholders parameters cause tags with the given {@linkplain #value() name} to be
 * resolved to a replacement provided argument by the caller.
 *
 * <p>The {@linkplain ComponentDeserializer#miniMessage(MiniMessage) mini message component
 * deserializer} allows the following types of component placeholders where the replacement is
 * constructed using {@linkplain Component Adventure's text component factory methods} and the
 * argument to the placeholder:
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
  @TagPattern
  String value();

  /**
   * Marks the annotated parameter as a choice placeholder.
   *
   * <p>Choice placeholders allow for conditional replacements.
   *
   * <h2>Boolean Choice Placeholders</h2>
   *
   * <p>The {@linkplain ComponentDeserializer#miniMessage(MiniMessage) mini message component
   * deserializer} allows for {@code boolean} choice placeholders where the placeholder argument is
   * used as the condition. {@code boolean} choice placeholders are defined as follows:
   *
   * <pre>{@code
   * @Configuration
   * interface YesNoConfiguration {
   *   @Property("yes-no")
   *   Component yesNo(@Placeholder.Choice("bool") boolean bool);
   * }
   * }</pre>
   *
   * The {@code yes-no} property may be a string that uses the tag with two arguments: the
   * replacement for the {@code true} case and the replacement for the {@code false} case.
   *
   * <pre>{@code
   * var source = ConfigurationNode.map(
   *     Map.entry("yes-no", ConfigurationNode.string("<bool:yes:no>)
   * );
   * YesNoConfiguration config = Warp.builder(YesNoConfiguration.class)
   *     .source(ConfigurationSource.of(source))
   *     .deserializer(Component.class, ConfigurationDeserializer.miniMessage())
   *     .build();
   * assert Component.text("yes").equals(config.yesNo(true));
   * assert Component.text("no").equals(config.yesNo(false));
   * }</pre>
   *
   * The exact behaviour of the tag is specified by {@linkplain Formatter#booleanChoice(String,
   * boolean) Adventure's boolean choice dynamic replacement}.
   *
   * <h2>Number Choice Placeholders</h2>
   *
   * <p>The mini message component deserializer also allows for number choice placeholders. This
   * works for {@code int}, {@code long}, {@code float} and {@code double}. The property may use the
   * tag with a single argument, the {@linkplain ChoiceFormat choice format} which specifies choices
   * based on the argument. A common use case is to express plurality:
   *
   * <pre>{@code
   * @Configuration
   * interface PeopleConfiguration {
   *   @Property("people")
   *   Component people(@Placeholder.Choice("people") int people);
   * }
   * }</pre>
   *
   * In this example, the {@code people} property uses the correct form of "no one", "someone" or
   * "everyone" based on the input:
   *
   * <pre>{@code
   * var source = ConfigurationNode.map(
   *     Map.entry("people", ConfigurationNode.string("<people:'0#no one|1#someone|1<everyone'>")
   * );
   * PeopleConfiguration config = Warp.builder(PeopleConfiguration.class)
   *     .source(ConfigurationSource.of(source))
   *     .deserializer(Component.class, ConfigurationDeserializer.miniMessage())
   *     .build();
   * assert Component.text("no one").equals(config.property(0));
   * assert Component.text("someone").equals(config.property(1));
   * assert Component.text("everyone").equals(config.property(2));
   * assert Component.text("everyone").equals(config.property(3));
   * }</pre>
   *
   * The exact behaviour of the tag is specified by {@linkplain Formatter#choice(String, Number)
   * Adventure's choice dynamic replacement}.
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
    @TagPattern
    String value();
  }

  /**
   * Marks the annotated method as a format placeholder.
   *
   * <p>Format placeholders let a specified format be applied to the placeholder argument.
   *
   * <p>The {@linkplain ComponentDeserializer#miniMessage(MiniMessage) mini message component
   * deserializer} allows for two types of format placeholders. Number format placeholders and date
   * format placeholders.
   *
   * <h2>Number Format Placeholders</h2>
   *
   * The mini message component deserializer allows for {@code int}, {@code long}, {@code float} and
   * {@code double} number format placeholders. The format of the replacement is specified by the
   * first argument. The details of the tag are specified by {@link Formatter#number(String, Number)
   * Adventure's number formatter dynamic replacement}.
   *
   * <h2>Date Format Placeholders</h2>
   *
   * The mini message component deserializer also allows any for date format placeholders for any
   * type assignable to {@link TemporalAccessor}. The exact behaviour of the tag is specified by
   * {@link Formatter#date(String, TemporalAccessor) Adventure's date formatter dynamic
   * replacement}.
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
    @TagPattern
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
    @TagPattern
    String value();
  }
}
