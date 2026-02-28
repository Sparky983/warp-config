# Getting Started

**Warp Config** is a general-purpose configuration mapper for defining type-safe, boilerplate-free configuration objects.

## Installation

Add the following to your build configuration:

::: code-group

```xml [pom.xml]
<dependency>
    <groupId>me.sparky983.warp</groupId>
    <artifactId>warp-yaml</artifactId>
    <version>0.2</version>
</dependency>
```

```kotlin [build.gradle.kts]
repositories {
    mavenCentral()
}

dependencies {
    implementation("me.sparky983.warp:warp-yaml:0.2")
}
```

```groovy [build.gradle]
repositories {
    mavenCentral()
}

dependencies {
    implementation 'me.sparky983.warp:warp-yaml:0.2'
}
```

:::

Currently, the only supported format is YAML.
