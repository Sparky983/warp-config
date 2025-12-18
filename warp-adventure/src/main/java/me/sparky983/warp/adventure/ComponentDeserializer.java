package me.sparky983.warp.adventure;

import me.sparky983.warp.Deserializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentDecoder;

/**
 * Factory utility class for creating {@link Component} {@link Deserializer Deserializers}.
 *
 * @since 0.2
 */
public final class ComponentDeserializer {
  /** Private constructor to prevent instantiation. */
  private ComponentDeserializer() {}

  /**
   * Creates a {@linkplain #miniMessage(MiniMessage) mini message deserializer} using the
   * {@linkplain MiniMessage#miniMessage() default mini message serializer} to deserialize
   * components.
   *
   * @return the deserializer
   * @see #miniMessage(MiniMessage)
   * @since 0.2
   */
  public static Deserializer<Component> miniMessage() {
    return miniMessage(MiniMessage.miniMessage());
  }

  /**
   * Creates a {@link Component} {@link Deserializer} using {@link MiniMessage} supporting
   * {@linkplain Placeholder placeholders}.
   *
   * @param miniMessage the mini message deserializer
   * @return the deserializer
   * @throws NullPointerException if the mini message is {@code null}.
   * @see Placeholder
   * @see Placeholder.Choice
   * @see Placeholder.Format
   * @see Placeholder.Parsed
   * @since 0.2
   */
  public static Deserializer<Component> miniMessage(final MiniMessage miniMessage) {
    return new MiniMessageDeserializer(miniMessage);
  }

  /**
   * Creates a generic generic {@link Component} {@link Deserializer} using a {@linkplain
   * ComponentDecoder generic component decoder}.
   *
   * <p>Note: the returned deserializer does not support {@linkplain Placeholder placeholders}.
   *
   * @param deserializer the component decoder
   * @return the component deserializer
   * @param <T> the type of the components
   * @throws NullPointerException if the deserializer is {@code null}.
   * @since 0.2
   */
  public static <T extends Component> Deserializer<T> deserializer(
      final ComponentDecoder<String, T> deserializer) {
    return new ComponentDecoderDeserializer<>(deserializer);
  }
}
