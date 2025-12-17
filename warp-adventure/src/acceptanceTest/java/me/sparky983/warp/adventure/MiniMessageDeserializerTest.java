package me.sparky983.warp.adventure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ChoiceFormat;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Map;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.ConfigurationBuilder;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Property;
import me.sparky983.warp.Warp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class MiniMessageDeserializerTest {
  <T> T createPlaceholderConfiguration(final Class<T> configurationClass, final String template)
      throws ConfigurationException {
    return Warp.builder(configurationClass)
        .source(
            ConfigurationSource.of(
                ConfigurationNode.map(Map.of("property", ConfigurationNode.string(template)))))
        .deserializer(Component.class, ComponentDeserializer.miniMessage())
        .build();
  }

  @Test
  void testDeserialization_NullNode() {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property();
    }

    final ConfigurationBuilder<TestConfiguration> builder =
        Warp.builder(TestConfiguration.class)
            .source(ConfigurationSource.of(ConfigurationNode.map()))
            .deserializer(Component.class, ComponentDeserializer.miniMessage());

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);
    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "property", ConfigurationError.error("Must be set to a value"))),
        thrown.errors());
  }

  @Test
  void testDeserialization_NullContext() {
    final Deserializer<Component> deserializer = ComponentDeserializer.miniMessage();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(ConfigurationNode.string(""), null));
  }

  @Test
  void testDeserialization_NonString() {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property();
    }

    final ConfigurationBuilder<TestConfiguration> builder =
        Warp.builder(TestConfiguration.class)
            .deserializer(Component.class, ComponentDeserializer.miniMessage())
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.of("property", ConfigurationNode.integer(0)))));

    final ConfigurationException thrown =
        assertThrows(ConfigurationException.class, builder::build);
    assertIterableEquals(
        List.of(ConfigurationError.group("property", ConfigurationError.error("Must be a string"))),
        thrown.errors());
  }

  @Test
  void testDeserialization_UnannotatedParameter() {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(String placeholder);
    }

    final ConfigurationBuilder<TestConfiguration> builder =
        Warp.builder(TestConfiguration.class)
            .deserializer(Component.class, ComponentDeserializer.miniMessage());

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialization_MultipleAnnotationsParameter() {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(
          @Placeholder("placeholder") @Placeholder.Parsed("placeholder") String placeholder);
    }

    final ConfigurationBuilder<TestConfiguration> builder =
        Warp.builder(TestConfiguration.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.of("property", ConfigurationNode.string("")))))
            .deserializer(Component.class, ComponentDeserializer.miniMessage());

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialization_NullMiniMessage() {
    assertThrows(NullPointerException.class, () -> ComponentDeserializer.miniMessage(null));
  }

  @Test
  void testDeserialization_ExplicitMiniMessage() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property();
    }

    final MiniMessage miniMessage =
        MiniMessage.builder()
            .editTags((tags) -> tags.tag("tag", Tag.inserting(Component.text("world"))))
            .build();
    final TestConfiguration configuration =
        Warp.builder(TestConfiguration.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(
                        Map.of("property", ConfigurationNode.string("Hello, <tag>!")))))
            .deserializer(Component.class, ComponentDeserializer.miniMessage(miniMessage))
            .build();

    assertEquals(Component.text("Hello, world!"), configuration.property());
  }

  @Test
  void testDeserialization_ComponentInvalidType() {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder("placeholder") Object placeholder);
    }

    final ConfigurationBuilder<TestConfiguration> builder =
        Warp.builder(TestConfiguration.class)
            .source(
                ConfigurationSource.of(
                    ConfigurationNode.map(Map.of("property", ConfigurationNode.string("")))))
            .deserializer(Component.class, ComponentDeserializer.miniMessage());

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialization_ComponentLikeSubtype() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder("placeholder") TranslatableComponent.Builder placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Hello, <placeholder>!");

    assertEquals(
        Component.text("Hello, ").append(Component.translatable("key")).append(Component.text("!")),
        configuration.property(Component.translatable().key("key")));
  }

  @Test
  void testDeserialization_ComponentString() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder("placeholder") @Nullable String placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Hello, <placeholder>!");

    assertEquals(Component.text("Hello, world!"), configuration.property("world"));
    assertEquals(Component.text("Hello, null!"), configuration.property(null));
  }

  @Test
  void testDeserialization_ComponentNumber() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder("placeholder") float placeholder);

      @Property("property")
      Component property(@Placeholder("placeholder") double placeholder);

      @Property("property")
      Component property(@Placeholder("placeholder") int placeholder);

      @Property("property")
      Component property(@Placeholder("placeholder") long placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Number: <placeholder>");

    assertEquals(Component.text("Number: 1.5"), configuration.property(1.5F));
    assertEquals(Component.text("Number: 1.5"), configuration.property(1.5));
    assertEquals(Component.text("Number: 15"), configuration.property(15));
    assertEquals(Component.text("Number: 15"), configuration.property(15L));
  }

  @Test
  void testDeserialization_ComponentChar() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder("placeholder") char placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Hello, <placeholder>!");

    assertEquals(Component.text("Hello, a!"), configuration.property('a'));
  }

  @Test
  void testDeserialization_ComponentBoolean() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder("placeholder") boolean placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Hello, <placeholder>!");

    assertEquals(Component.text("Hello, true!"), configuration.property(true));
    assertEquals(Component.text("Hello, false!"), configuration.property(false));
  }

  @Test
  void testDeserialization_ChoiceNumber() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder.Choice("placeholder") int placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(
            TestConfiguration.class,
            "<placeholder:'0#no one|1#someone|1<everyone'>");

    assertEquals(Component.text("no one"), configuration.property(0));
    assertEquals(Component.text("someone"), configuration.property(1));
    assertEquals(Component.text("everyone"), configuration.property(2));
    assertEquals(Component.text("everyone"), configuration.property(3));
  }

  @Test
  void testDeserialization_ChoiceBoolean() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder.Choice("placeholder") boolean placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "<placeholder:yes:no>");

    assertEquals(Component.text("yes"), configuration.property(true));
    assertEquals(Component.text("no"), configuration.property(false));
  }

  @Test
  void testDeserialization_FormatInvalidType() {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder.Format("placeholder") Object placeholder);
    }

    final ConfigurationBuilder<TestConfiguration> builder =
        Warp.builder(TestConfiguration.class)
            .deserializer(Component.class, ComponentDeserializer.miniMessage());

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialization_FormatNumber() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder.Format("placeholder") float placeholder);

      @Property("property")
      Component property(@Placeholder.Format("placeholder") double placeholder);

      @Property("property")
      Component property(@Placeholder.Format("placeholder") int placeholder);

      @Property("property")
      Component property(@Placeholder.Format("placeholder") long placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Number: <placeholder:00.00>");

    assertEquals(Component.text("Number: 01.50"), configuration.property(1.5F));
    assertEquals(Component.text("Number: 01.50"), configuration.property(1.5));
    assertEquals(Component.text("Number: 15.00"), configuration.property(15));
    assertEquals(Component.text("Number: 15.00"), configuration.property(15L));
  }

  @Test
  void testDeserialization_FormatDate() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder.Format("placeholder") LocalDateTime placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Date: <placeholder:'yyyy-MM-dd HH:mm:ss'>");

    assertEquals(
        Component.text("Date: 2025-12-17 18:36:03"),
        configuration.property(LocalDateTime.of(2025, 12, 17, 18, 36, 3)));
  }

  @Test
  void testDeserialization_StringParsed() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder.Parsed("placeholder") @Nullable String placeholder);
    }

    final TestConfiguration configuration =
        createPlaceholderConfiguration(TestConfiguration.class, "Hello, <placeholder>!");

    assertEquals(
        Component.text("Hello, ")
            .append(Component.text("world").color(NamedTextColor.RED))
            .append(Component.text("!")),
        configuration.property("<red>world</red>"));
    assertEquals(Component.text("Hello, null!"), configuration.property(null));
  }

  @Test
  void testDeserialization_ParsedNonString() {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property(@Placeholder.Parsed("placeholder") Object placeholder);
    }

    final ConfigurationBuilder<TestConfiguration> builder =
        Warp.builder(TestConfiguration.class)
            .deserializer(Component.class, ComponentDeserializer.miniMessage());

    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  void testDeserialization_NullPlaceholder() throws ConfigurationException {
    @Configuration
    interface TestConfiguration {
      @Property("property")
      Component property1(@Placeholder("placeholder") @Nullable Component placeholder);

      @Property("property")
      Component property2(@Placeholder("placeholder") @Nullable String placeholder);

      @Property("property")
      Component property3(@Placeholder.Format("placeholder") @Nullable TemporalAccessor placeholder);

      @Property("property")
      Component property4(@Placeholder.Parsed("placeholder") @Nullable String placeholder);
    }

    final TestConfiguration configuration = createPlaceholderConfiguration(TestConfiguration.class, "<placeholder>");

    assertEquals(Component.text("null"), configuration.property1(null));
    assertEquals(Component.text("null"), configuration.property2(null));
    assertEquals(Component.text("null"), configuration.property3(null));
    assertEquals(Component.text("null"), configuration.property4(null));
  }
}
