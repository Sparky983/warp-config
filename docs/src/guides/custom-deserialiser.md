# Custom Deserialiser

## Deserialisation Process

The process of converting a `ConfigurationNode` into an object occurs in two stages:

1. **Deserialization**: where the node is parsed or validated to turn it into a useful form
2. **Rendering**: where the actual value is returned

These two stages allow upfront validation and additional processing when a property method is called.

## Implementing a Deserializer

To create a custom deserialiser, implement the `deserialize` method of the `Deserializer` interface.

::: code-group

```java [Java]
import java.util.UUID;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;

class UuidDeserializer implements Deserializer<UUID> {
  @Override
  public Renderer<UUID> deserialize(ConfigurationNode node, Deserializer.Context context)
      throws DeserializationException {
    try {
      return Renderer.of(UUID.fromString(node.asString()));
    } catch (IllegalArgumentException e) {
      throw new DeserializationException(ConfigurationError.error("Must be a valid UUID"));
    }
  }
}
```

```kotlin [Kotlin]
import java.util.UUID
import me.sparky983.warp.ConfigurationError
import me.sparky983.warp.ConfigurationNode
import me.sparky983.warp.DeserializationException
import me.sparky983.warp.Deserializer
import me.sparky983.warp.Renderer

class UuidDeserializer : Deserializer<UUID> {
  override fun deserialize(
    node: ConfigurationNode,
    context: Deserializer.Context
  ): Renderer<UUID> {
    try {
      return Renderer.of(UUID.fromString(node.asString()))
    } catch (e: IllegalArgumentException) {
      throw DeserializationException(ConfigurationError.error("Must be a valid UUID"))
    }
  }
}
```

:::

`DeserializationException` represents a structured error used to build detailed error messages. The error may include messages or labeled groups of errors.

The new deserializer can then be used in a configuration:

::: code-group

```java [Java]
import java.util.UUID;
import me.sparky983.warp.Configuration;
import me.sparky983.warp.Property;
import me.sparky983.warp.Warp;

@Configuration
interface UuidConfiguration {
  @Property("uuid")
  UUID uuid();
}

UuidConfiguration config = Warp.builder(UuidConfiguration.class)
    .source(...)
    .deserializer(UUID.class, new UuidDeserializer())
    .build();

UUID uuid = config.uuid();
```

```kotlin [Kotlin]
import java.util.UUID
import me.sparky983.warp.Configuration
import me.sparky983.warp.Property
import me.sparky983.warp.Warp

@Configuration
interface UuidConfiguration {
  @Property("uuid")
  fun uuid(): UUID
}

val config = Warp.builder(UuidConfiguration::class.java)
    .source(...)
    .deserializer(UUID::class.java, UuidDeserializer())
    .build()

val uuid = config.uuid()
```

:::
