package me.sparky983.warp.adventure;

import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.Objects;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.adventure.Placeholder.Choice;
import me.sparky983.warp.adventure.Placeholder.Format;
import me.sparky983.warp.adventure.Placeholder.Parsed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jspecify.annotations.Nullable;

/**
 * A component deserializer that uses the {@link MiniMessage} format and supports {@linkplain
 * Placeholder placeholders}.
 */
final class MiniMessageDeserializer implements Deserializer<Component> {
  private static final Map<Class<?>, PlaceholderKind> FINAL_COMPONENT_CLASSES =
      Map.of(
          String.class, PlaceholderKind.COMPONENT_STRING,
          boolean.class, PlaceholderKind.COMPONENT_BOOLEAN,
          char.class, PlaceholderKind.COMPONENT_CHAR,
          float.class, PlaceholderKind.COMPONENT_NUMBER,
          double.class, PlaceholderKind.COMPONENT_NUMBER,
          int.class, PlaceholderKind.COMPONENT_NUMBER,
          long.class, PlaceholderKind.COMPONENT_NUMBER);
  private static final Map<Class<?>, PlaceholderKind> CHOICE_CLASSES =
      Map.of(
          boolean.class, PlaceholderKind.CHOICE_BOOLEAN,
          float.class, PlaceholderKind.CHOICE_NUMBER,
          double.class, PlaceholderKind.CHOICE_NUMBER,
          int.class, PlaceholderKind.CHOICE_NUMBER,
          long.class, PlaceholderKind.CHOICE_NUMBER);
  private static final Map<Class<?>, PlaceholderKind> FINAL_FORMAT_CLASSES =
      Map.of(
          float.class, PlaceholderKind.FORMAT_NUMBER,
          double.class, PlaceholderKind.FORMAT_NUMBER,
          int.class, PlaceholderKind.FORMAT_NUMBER,
          long.class, PlaceholderKind.FORMAT_NUMBER);

  static {
    for (final Class<?> finalClass : FINAL_COMPONENT_CLASSES.keySet()) {
      assert Modifier.isFinal(finalClass.getModifiers());
    }
  }

  private final MiniMessage miniMessage;

  /**
   * Constructs a {@code MiniMessageDeserializer}.
   *
   * @param miniMessage the mini message deserializer
   * @throws NullPointerException if the mini message deserializer is {@code null}.
   */
  MiniMessageDeserializer(final MiniMessage miniMessage) {
    Objects.requireNonNull(miniMessage, "miniMessage cannot be null");

    this.miniMessage = miniMessage;
  }

  @Override
  public Renderer<Component> deserialize(
      final @Nullable ConfigurationNode node, final Context deserializerContext)
      throws DeserializationException {
    Objects.requireNonNull(deserializerContext, "context cannot be null");

    final Parameter[] parameters = deserializerContext.parameters();
    final PlaceholderFactory[] placeholders = new PlaceholderFactory[parameters.length];

    for (int i = 0; i < parameters.length; i++) {
      final Parameter parameter = parameters[i];

      final Placeholder placeholder = parameter.getAnnotation(Placeholder.class);
      final Choice choice = parameter.getAnnotation(Choice.class);
      final Format format = parameter.getAnnotation(Format.class);
      final Parsed parsed = parameter.getAnnotation(Parsed.class);

      final Class<?> parameterType = parameter.getType();

      final PlaceholderFactory namedPlaceholder;

      if (placeholder != null && parsed == null && format == null && choice == null) {
        final PlaceholderKind kind = FINAL_COMPONENT_CLASSES.get(parameterType);
        if (kind != null) {
          namedPlaceholder = new PlaceholderFactory(placeholder.value(), kind);
        } else if (ComponentLike.class.isAssignableFrom(parameterType)) {
          namedPlaceholder = new PlaceholderFactory(placeholder.value(), PlaceholderKind.COMPONENT);
        } else {
          throw new IllegalStateException(
              "@Placeholder("
                  + placeholder.value()
                  + ") parameter from method "
                  + getMethodNameFrom(parameter)
                  + " must be int, long, float, double, String or ComponentLike");
        }
      } else if (placeholder == null && choice != null && format == null && parsed == null) {
        final PlaceholderKind kind = CHOICE_CLASSES.get(parameterType);
        if (kind == null) {
          throw new IllegalStateException(
              "@Placeholder.Choice("
                  + choice.value()
                  + ") parameter from method "
                  + getMethodNameFrom(parameter)
                  + " must be int, long, float, double or boolean");
        }
        namedPlaceholder = new PlaceholderFactory(choice.value(), kind);
      } else if (placeholder == null && choice == null && format != null && parsed == null) {
        final PlaceholderKind kind = FINAL_FORMAT_CLASSES.get(parameterType);
        if (kind != null) {
          namedPlaceholder = new PlaceholderFactory(format.value(), kind);
        } else if (TemporalAccessor.class.isAssignableFrom(parameterType)) {
          namedPlaceholder = new PlaceholderFactory(format.value(), PlaceholderKind.FORMAT_DATE);
        } else {
          throw new IllegalStateException(
              "@Placeholder.Format("
                  + format.value()
                  + ") parameter from method "
                  + getMethodNameFrom(parameter)
                  + " must be int, long, float, double or TemporalAccessor");
        }
      } else if (placeholder == null && choice == null && format == null && parsed != null) {
        if (!parameterType.equals(String.class)) {
          throw new IllegalStateException(
              "@Placeholder.Parsed("
                  + parsed.value()
                  + ") parameter from method "
                  + getMethodNameFrom(parameter)
                  + " must be String");
        }
        namedPlaceholder = new PlaceholderFactory(parsed.value(), PlaceholderKind.PARSED_STRING);
      } else {
        throw new IllegalStateException(
            "All parameters of @Property method "
                + getMethodNameFrom(parameter)
                + " must have a placeholder annotation");
      }
      placeholders[i] = namedPlaceholder;
    }

    if (node == null) {
      throw new DeserializationException(ConfigurationError.error("Must be set to a value"));
    }

    final String string = node.asString();

    return (rendererContext) -> {
      final Object[] arguments = rendererContext.arguments();
      final TagResolver[] tagResolvers = new TagResolver[placeholders.length];
      for (int i = 0; i < parameters.length; i++) {
        tagResolvers[i] = placeholders[i].tag(arguments[i]);
      }
      return miniMessage.deserialize(string, tagResolvers);
    };
  }

  private String getMethodNameFrom(final Parameter parameter) {
    final Executable executable = parameter.getDeclaringExecutable();
    return executable.getDeclaringClass().getCanonicalName() + "." + executable.getName();
  }

  /**
   * Creates {@link TagResolver TagResolvers} for placeholders.
   *
   * @param name the name of the placeholder
   * @param kind the type of placeholder
   */
  private record PlaceholderFactory(String name, PlaceholderKind kind) {
    private PlaceholderFactory {
      Objects.requireNonNull(name, "name cannot be null");
      Objects.requireNonNull(kind, "kind cannot be null");
    }

    private TagResolver tag(final @Nullable Object value) {
      if (value == null) {
        return component(name, Component.text("null"));
      }
      return switch (kind) {
        case COMPONENT_STRING -> unparsed(name, String.valueOf(value));
        case COMPONENT_NUMBER, COMPONENT_CHAR, COMPONENT_BOOLEAN -> component(
            name, Component.text(String.valueOf(value)));
        case COMPONENT -> {
          final Component component = Objects.requireNonNull(((ComponentLike) value).asComponent());
          yield component(name, component);
        }
        case CHOICE_NUMBER -> Formatter.choice(name, (Number) value);
        case CHOICE_BOOLEAN -> Formatter.booleanChoice(name, (Boolean) value);
        case FORMAT_NUMBER -> Formatter.number(name, (Number) value);
        case FORMAT_DATE -> Formatter.date(name, (TemporalAccessor) value);
        case PARSED_STRING -> parsed(name, String.valueOf(value));
      };
    }
  }

  private enum PlaceholderKind {
    COMPONENT,
    COMPONENT_STRING,
    COMPONENT_NUMBER,
    COMPONENT_CHAR,
    COMPONENT_BOOLEAN,
    CHOICE_NUMBER,
    CHOICE_BOOLEAN,
    FORMAT_NUMBER,
    FORMAT_DATE,
    PARSED_STRING
  }
}
