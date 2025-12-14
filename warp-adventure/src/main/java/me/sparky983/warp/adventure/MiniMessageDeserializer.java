package me.sparky983.warp.adventure;

import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;

import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.Nullable;

final class MiniMessageDeserializer implements Deserializer<Component> {
  private static final Map<Class<?>, PlaceholderKind> FINAL_PLACEHOLDER_CLASSES =
      Map.of(
          String.class, PlaceholderKind.STRING_COMPONENT,
          boolean.class, PlaceholderKind.BOOLEAN_COMPONENT,
          char.class, PlaceholderKind.CHAR_COMPONENT,
          float.class, PlaceholderKind.NUMBER_FORMAT,
          double.class, PlaceholderKind.NUMBER_FORMAT,
          int.class, PlaceholderKind.NUMBER_FORMAT,
          long.class, PlaceholderKind.NUMBER_FORMAT);

  static {
    for (final Class<?> finalClass : FINAL_PLACEHOLDER_CLASSES.keySet()) {
      assert Modifier.isFinal(finalClass.getModifiers());
    }
  }

  private final MiniMessage miniMessage;

  MiniMessageDeserializer(final MiniMessage miniMessage) {
    Objects.requireNonNull(miniMessage, "miniMessage cannot be null");

    this.miniMessage = miniMessage;
  }

  @Override
  public Renderer<Component> deserialize(
      final @Nullable ConfigurationNode node, final Context deserializerContext)
      throws DeserializationException {
    Objects.requireNonNull(deserializerContext, "context cannot be null");

    if (node == null) {
      throw new DeserializationException(ConfigurationError.error("Must be set to a value"));
    }

    final String string = node.asString();

    final Parameter[] parameters = deserializerContext.parameters();
    final @Nullable NamedPlaceholder[] placeholders = new NamedPlaceholder[parameters.length];

    for (int i = 0; i < parameters.length; i++) {
      final Parameter parameter = parameters[i];

      final Placeholder placeholder = parameter.getAnnotation(Placeholder.class);
      final Placeholder.Parsed parsed = parameter.getAnnotation(Placeholder.Parsed.class);

      final Class<?> parameterType = parameter.getType();

      final NamedPlaceholder namedPlaceholder;

      if (placeholder != null) {
        if (parsed != null) {
          throw new IllegalStateException(
              "@Property method declared a parameter with both @Placeholder and @Placeholder.Parsed");
        }

        final PlaceholderKind placeholderKind;

        final PlaceholderKind finalKind = FINAL_PLACEHOLDER_CLASSES.get(parameterType);
        if (finalKind != null) {
          placeholderKind = finalKind;
        } else if (ComponentLike.class.isAssignableFrom(parameterType)) {
          placeholderKind = PlaceholderKind.COMPONENT;
        } else {
          throw new IllegalStateException(
              "@Property method declared parameter with @Placeholder but type is not a primitive, String or ComponentLike");
        }

        namedPlaceholder = new NamedPlaceholder(placeholder.value(), placeholderKind);
      } else {
        if (parsed == null) {
          throw new IllegalStateException(
              "@Property method declared a parameter not annotated with @Placeholder or @Placeholder.Parsed");
        }

        if (parameterType.equals(String.class)) {
          namedPlaceholder = new NamedPlaceholder(parsed.value(), PlaceholderKind.STRING_PARSED);
        } else {
          throw new IllegalStateException(
              "@Property method declared parameter with @Placeholder.Parsed but type is not String");
        }
      }
      placeholders[i] = namedPlaceholder;
    }

    return (rendererContext) -> {
      final Object[] arguments = rendererContext.arguments();
      final TagResolver[] tagResolvers = new TagResolver[placeholders.length];
      for (int i = 0; i < parameters.length; i++) {
        tagResolvers[i] = placeholders[i].tag(arguments[i]);
      }
      return miniMessage.deserialize(string, tagResolvers);
    };
  }

  private record NamedPlaceholder(String name, PlaceholderKind kind) {
    private TagResolver tag(final @Nullable Object value) {
      return switch (kind) {
        case STRING_PARSED -> parsed(name, String.valueOf(value));
        case STRING_COMPONENT -> unparsed(name, String.valueOf(value));
        case NUMBER_FORMAT -> {
          assert value != null : "primitive value should never be null";
          yield Formatter.number(name, (Number) value);
        }
        case BOOLEAN_COMPONENT, CHAR_COMPONENT -> {
          assert value != null : "primitive value should never be null";
          yield component(name, Component.text(String.valueOf(value)));
        }
        case COMPONENT -> {
          final Component component = ComponentLike.unbox((ComponentLike) value);
          yield component(name, component == null ? Component.text("null") : component);
        }
      };
    }
  }

  private enum PlaceholderKind {
    STRING_PARSED,
    STRING_COMPONENT,
    NUMBER_FORMAT,
    BOOLEAN_COMPONENT,
    CHAR_COMPONENT,
    COMPONENT
  }
}
