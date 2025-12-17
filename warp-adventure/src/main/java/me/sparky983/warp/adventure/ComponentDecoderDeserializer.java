package me.sparky983.warp.adventure;

import java.util.Objects;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import org.jspecify.annotations.Nullable;

/**
 * A generic component deserializer for any {@link ComponentDecoder}.
 *
 * @param <T> the component type
 */
final class ComponentDecoderDeserializer<T extends Component> implements Deserializer<T> {
  private final ComponentDecoder<String, T> decoder;

  /**
   * Constructs a {@code ComponentDecoderDeserializer}.
   *
   * @param decoder the generic component deserializer
   * @throws NullPointerException if the decoder is {@code null}.
   */
  ComponentDecoderDeserializer(final ComponentDecoder<String, T> decoder) {
    Objects.requireNonNull(decoder, "decoder cannot be null");

    this.decoder = decoder;
  }

  @Override
  public Renderer<T> deserialize(final @Nullable ConfigurationNode node, final Context context)
      throws DeserializationException {
    Objects.requireNonNull(context, "context cannot be null");

    if (node == null) {
      throw new DeserializationException(ConfigurationError.error("Must be set to a value"));
    }

    return Renderer.of(decoder.deserialize(node.asString()));
  }
}
