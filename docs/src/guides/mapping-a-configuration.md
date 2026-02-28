# Mapping a Configuration

A configuration mapping is defined by using the `@Configuration` annotation. `@Property`-annotated methods define the properties of the mapping.

::: code-group

```java [Java]
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;

@Configuration
interface AppConfiguration {
  @Property("name")
  String name();

  @Property("debug")
  boolean debugMode();
}
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property

@Configuration
interface ApplicationConfiguration {
    @Property("name")
    fun name(): String

    @Property("debug")
    fun debugMode(): Boolean
}
```

:::

Then, create a configuration in any supported format. For this example, we will use YAML.

```yaml
name: My Application
debug: true
```

And finally, map it to your `ApplicationConfiguration` using `Warp.builder()`:

::: code-group

```java [Java]
import me.sparky983.warp.Warp;
import me.sparky983.warp.yaml.YamlConfigurationSource;

AppConfiguration configuration = Warp.builder(AppConfiguration.class)
    .source(YamlConfigurationSource.read(...))
    .build();
```

```kotlin [Kotlin]
import me.sparky983.warp.Warp
import me.sparky983.warp.yaml.YamlConfigurationSource

val configuration = Warp.builder(AppConfiguration::class.java)
    .source(YamlConfigurationSource.read(...))
    .build()
```

:::

`YamlConfigurationSource.read(...)` accepts any of the following:

- `Path`
- `InputStream`
- `Reader`

## Nested Configurations

Configurations can be nested as long as the type of a property is annotated with `@Configuration`.

::: code-group

```java [Java]
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;

@Configuration
interface AppConfiguration {
  @Property("server")
  ServerConfiguration server();
}

@Configuration
interface ServerConfiguration {
  @Property("host")
  String host();

  @Property("port")
  int port();
}
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property

@Configuration
interface AppConfiguration {
    @Property("server")
    fun server(): ServerConfiguration
}

@Configuration
interface ServerConfiguration {
    @Property("host")
    fun host(): String

    @Property("port")
    fun port(): Int
}
```

:::

## Collections

Properties may use collection types to deserialize lists or maps:

::: code-group

```java [Java]
import java.util.List;
import java.util.Map;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;

@Configuration
interface AppConfiguration {
  @Property("people")
  List<Person> people();

  @Property("job-descriptions")
  Map<Job, String> jobDescriptions();
}

enum Job {
  SOFTWARE_ENGINEER,
  // ...
}
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property

@Configuration
interface AppConfiguration {
  @Property("people")
  fun people(): List<Person>

  @Property("job-descriptions")
  fun jobDescriptions(): Map<Job, String>
}

enum class Job {
  SOFTWARE_ENGINEER,
  // ...
}
```

:::

Note that maps are not checked to be exhaustive.

## Defaults

Defaults can be specified by providing a default implementation of a property method:

::: code-group

```java [Java]
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;

@Configuration
interface AppConfiguration {
  @Property("name")
  String name();

  @Property("debug")
  default boolean debugMode() {
    return false;
  }
}
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property

@Configuration
interface ApplicationConfiguration {
    @Property("name")
    fun name(): String

    @Property("debug")
    fun debugMode(): Boolean = false
}
```

:::

Alternatively, `java.util.Optional` can be used to make a property optional.

## Error Messages

Warp provides detailed, structured error messages if the configuration is invalid:

::: code-group

```java [Java]
import java.util.List;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;

@Configuration
interface TeamConfiguration {
  @Property("name")
  String name();

  @Property("color")
  Color color();

  @Property("description")
  String description();

  @Property("captain")
  Player captain();

  @Property("players")
  List<Player> players();

  enum Color {
    BLUE,
    RED
  }

  @Configuration
  interface Player {
    @Property("name")
    String name();
  }
}
```

```kotlin [Kotlin]
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property

@Configuration
interface TeamConfiguration {
  @Property("name")
  fun name(): String

  @Property("color")
  fun color(): Color

  @Property("description")
  fun description(): String

  @Property("captain")
  fun captain(): Player

  @Property("players")
  fun players(): List<Player>

  enum class Color {
    BLUE,
    RED
  }

  @Configuration
  interface Player {
    @Property("name")
    fun name(): String
  }
}
```

:::

Deserializing the following YAML configuration:

```yaml
name: Pink Team
color: PINK
captain:
  - name: Pink Captain
players:
  - name: Pink Player 1
  - name: Pink Player 2
    ranking: 1
```

Throws a `ConfigurationException`. `getMessage()` returns:

```ansi
[91m- color: PINK is not a valid value
- description: Must be set to a value
- captain: Must be a map
  - name: Must be set to a value
  - age: Must be set to a value
- players:
  - 1:
    - ranking: Unknown property[0m
```
