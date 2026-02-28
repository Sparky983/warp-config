---
description: Support for net.kyori:adventure types
---

# Adventure

## Installation

Add the following to your build configuration:

::: code-group

```xml [pom.xml]
<dependencies>
    <dependency>
        <groupId>me.sparky983.warp</groupId>
        <artifactId>warp-adventure</artifactId>
        <version>0.2</version>
    </dependency>
</dependencies>
```

```kotlin [build.gradle.kts]
repositories {
    mavenCentral()
}

dependencies {
    implementation("me.sparky983.warp:warp-adventure:0.2")
}
```

```groovy [build.gradle]
repositories {
    mavenCentral()
}

dependencies {
    implementation 'me.sparky983.warp:warp-adventure:0.2'
}
```

:::

## Mini Message

Warp provides a deserializer for Adventure's MiniMessage format. Warp's architecture allows strong support for MiniMessage features such as placeholders and formatters.

### Placeholders

A property method returning `Component` may declare any number of parameters to represent placeholders used during deserialization.

#### Regular Placeholders

A regular placeholder or component placeholder replaces the placeholder with the provided argument. The parameter can be most common types as well as `Component`.

::: code-group

```java [Java]
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;
import me.sparky983.warp.Warp;
import me.sparky983.warp.adventure.ComponentDeserializer;
import me.sparky983.warp.adventure.Placeholder;
import net.kyori.adventure.text.Component;

@Configuration
interface Messages {
  @Property("greet")
  Component greetString(@Placeholder("name") String name);

  @Property("greet")
  Component greetComponent(@Placeholder("name") Component name);
}

Messages messages = Warp.builder(Messages.class)
    .source(...) // greet: Hello <name>
    .deserializer(Component.class, ComponentDeserializer.miniMessage())
    .build();

assert Component.text("Hello world").equals(messages.greetString("world"));
assert Component.text("Hello ").append(Component.text("world", RED))
    .equals(messages.greetComponent(Component.text("world", RED)));
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property
import me.sparky983.warp.Warp
import me.sparky983.warp.adventure.ComponentDeserializer
import me.sparky983.warp.adventure.Placeholder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED

@Configuration
interface Messages {
  @Property("greet")
  fun greetString(@Placeholder("name") name: String): Component

  @Property("greet")
  fun greetComponent(@Placeholder("name") name: Component): Component
}

val messages = Warp.builder(Messages::class.java)
    .source(...) // greet: Hello <name>
    .deserializer(Component::class.java, ComponentDeserializer.miniMessage())
    .build()

assert(Component.text("Hello world") == messages.greetString("world"))
assert(
  Component.text("Hello ").append(Component.text("world", RED)) ==
    messages.greetComponent(Component.text("world", RED))
)
```

:::

#### Parsed Placeholders

`@Placeholder.Parsed` is used to insert a placeholder that will be parsed by MiniMessage:

::: code-group

```java [Java]
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;
import me.sparky983.warp.Warp;
import me.sparky983.warp.adventure.ComponentDeserializer;
import me.sparky983.warp.adventure.Placeholder;
import net.kyori.adventure.text.Component;

@Configuration
interface Messages {
  @Property("greet")
  Component greet(@Placeholder.Parsed("name") String name);
}

Messages messages = Warp.builder(Messages.class)
    .source(...) // greet: Hello <name>
    .deserializer(Component.class, ComponentDeserializer.miniMessage())
    .build();

assert Component.text("Hello ").append(Component.text("world", RED))
    .equals(messages.greet("<red>world"));
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property
import me.sparky983.warp.Warp
import me.sparky983.warp.adventure.ComponentDeserializer
import me.sparky983.warp.adventure.Placeholder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED

@Configuration
interface Messages {
  @Property("greet")
  fun greet(@Placeholder.Parsed("name") name: String): Component
}

val messages = Warp.builder(Messages::class.java)
    .source(...) // greet: Hello <name>
    .deserializer(Component::class.java, ComponentDeserializer.miniMessage())
    .build()

assert(
  Component.text("Hello ").append(Component.text("world", RED)) ==
    messages.greet("<red>world")
)
```

:::

#### Choice Placeholders

Choice placeholders allow conditional replacements based on the value:

::: code-group

```java [Java]
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;
import me.sparky983.warp.Warp;
import me.sparky983.warp.adventure.ComponentDeserializer;
import me.sparky983.warp.adventure.Placeholder;
import net.kyori.adventure.text.Component;

@Configuration
interface Messages {
  @Property("people")
  Component people(@Placeholder.Choice("people") int numberOfPeople);

  @Property("yes-no")
  Component yesNo(@Placeholder.Choice("bool") boolean bool);
}

Messages messages = Warp.builder(Messages.class)
    .source(...) // people: <people:'0#no one|1#someone|1<everyone'>
                 // yes-no: <bool:yes:no>
    .deserializer(Component.class, ComponentDeserializer.miniMessage())
    .build();

assert Component.text("no one").equals(messages.people(0));
assert Component.text("someone").equals(messages.people(1));
assert Component.text("everyone").equals(messages.people(2));
assert Component.text("everyone").equals(messages.people(3));
assert Component.text("yes").equals(messages.yesNo(true));
assert Component.text("no").equals(messages.yesNo(false));
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property
import me.sparky983.warp.Warp
import me.sparky983.warp.adventure.ComponentDeserializer
import me.sparky983.warp.adventure.Placeholder
import net.kyori.adventure.text.Component

@Configuration
interface Messages {
  @Property("people")
  fun people(@Placeholder.Choice("people") numberOfPeople: Int): Component

  @Property("yes-no")
  fun yesNo(@Placeholder.Choice("bool") bool: Boolean): Component
}

val messages = Warp.builder(Messages::class.java)
    .source(...) // people: <people:'0#no one|1#someone|1<everyone'>
                 // yes-no: <bool:yes:no>
    .deserializer(Component::class.java, ComponentDeserializer.miniMessage())
    .build()

assert(Component.text("no one") == messages.people(0))
assert(Component.text("someone") == messages.people(1))
assert(Component.text("everyone") == messages.people(2))
assert(Component.text("everyone") == messages.people(3))
assert(Component.text("yes") == messages.yesNo(true))
assert(Component.text("no") == messages.yesNo(false))
```

:::

#### Format Placeholders

Format placeholders apply a format before replacing the placeholder:

::: code-group

```java [Java]
import java.time.LocalDate;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;
import me.sparky983.warp.Warp;
import me.sparky983.warp.adventure.ComponentDeserializer;
import me.sparky983.warp.adventure.Placeholder;
import net.kyori.adventure.text.Component;

@Configuration
interface Messages {
  @Property("balance")
  Component balance(@Placeholder.Format("balance") double balance);

  @Property("date")
  Component date(@Placeholder.Format("date") LocalDate date);
}

Messages messages = Warp.builder(Messages.class)
    .source(...) // balance: Your balance is $<balance:'#.00'>
                 // date: Today's date is <date:'d/M/yyyy'>
    .deserializer(Component.class, ComponentDeserializer.miniMessage())
    .build();

assert Component.text("Your balance is $10.50").equals(messages.balance(10.50));
assert Component.text("Today's date is 19/12/2025")
    .equals(messages.date(LocalDate.of(2025, 12, 19)));
```

```kotlin [Kotlin]
import java.time.LocalDate
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property
import me.sparky983.warp.Warp
import me.sparky983.warp.adventure.ComponentDeserializer
import me.sparky983.warp.adventure.Placeholder
import net.kyori.adventure.text.Component

@Configuration
interface Messages {
  @Property("balance")
  fun balance(@Placeholder.Format("balance") balance: Double): Component

  @Property("date")
  fun date(@Placeholder.Format("date") date: LocalDate): Component
}

val messages = Warp.builder(Messages::class.java)
    .source(...) // balance: Your balance is $<balance:'#.00'>
                 // date: Today's date is <date:'d/M/yyyy'>
    .deserializer(Component::class.java, ComponentDeserializer.miniMessage())
    .build()

assert(Component.text("Your balance is $10.50") == messages.balance(10.50))
assert(Component.text("Today's date is 19/12/2025") == messages.date(LocalDate.of(2025, 12, 19)))
```

:::

## Generic Deserializer

A Warp deserializer can be made from any Adventure component deserializer.

::: code-group

```java [Java]
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;
import me.sparky983.warp.Warp;
import me.sparky983.warp.adventure.ComponentDeserializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@Configuration
interface Messages {
  @Property("startup-log")
  Component startup();
}

Messages messages = Warp.builder(Messages.class)
    .source(...) // startup-log: &7Plugin successfully started
    .deserializer(
        Component.class,
        ComponentDeserializer.deserializer(LegacyComponentSerializer.legacyAmpersand()))
    .build();

assert Component.text("Plugin successfully started", GRAY).equals(messages.startup());
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property
import me.sparky983.warp.Warp
import me.sparky983.warp.adventure.ComponentDeserializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

@Configuration
interface Messages {
  @Property("startup-log")
  fun startup(): Component
}

val messages = Warp.builder(Messages::class.java)
    .source(...) // startup-log: &7Plugin successfully started
    .deserializer(
      Component::class.java,
      ComponentDeserializer.deserializer(LegacyComponentSerializer.legacyAmpersand())
    )
    .build()

assert(Component.text("Plugin successfully started", GRAY) == messages.startup())
```

:::
