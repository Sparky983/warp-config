package me.sparky983.warp.adventure;

import me.sparky983.warp.Deserializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentDecoder;

public final class ComponentDeserializer {
  public static Deserializer<Component> miniMessage() {
    return miniMessage(MiniMessage.miniMessage());
  }

  public static Deserializer<Component> miniMessage(final MiniMessage miniMessage) {
    return new MiniMessageDeserializer(miniMessage);
  }

  public static <T extends Component> Deserializer<T> deserializer(final ComponentDecoder<String, T> deserializer) {
    return new ComponentDecoderDeserializer<>(deserializer);
  }
}
