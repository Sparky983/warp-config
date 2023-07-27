package me.sparky983.warp.internal.node;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import me.sparky983.warp.ConfigurationNode;

/** The default implementation of {@link Map}.
 *
 * @param values the values*/
public record DefaultMapNode(@Override java.util.Map<java.lang.String, ConfigurationNode> values)
    implements ConfigurationNode.Map {
  /**
   * Constructs a {@code DefaultMapNode}.
   *
   * @param values the values; changes in this map will not be reflected in the constructed
   * {@code DefaultMapNode}
   * @throws NullPointerException if the values map is {@code null} or has an entry that contains
   *     {@code null}.
   */
  public DefaultMapNode(final java.util.Map<java.lang.String, ConfigurationNode> values) {
    this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
  }

  @Override
  public Optional<ConfigurationNode> get(final java.lang.String key) {
    return Optional.ofNullable(values.get(key));
  }

  @Override
  public Iterable<Entry> entries() {
    return () ->
        values.entrySet().stream()
            .map((entry) -> Map.entry(entry.getKey(), entry.getValue()))
            .iterator();
  }

  /** The default implementation of {@link Builder}. */
  public static final class DefaultBuilder implements Builder {
    private final java.util.Map<java.lang.String, ConfigurationNode> values = new LinkedHashMap<>();

/**
* Constructs a {@code DefaultBuilder}.
*/
    public DefaultBuilder() {}

    @Override
    public Builder entry(final java.lang.String key, final ConfigurationNode value) {
      Objects.requireNonNull(key, "key cannot be null");
      Objects.requireNonNull(value, "value cannot be null");
      values.put(key, value);
      return this;
    }

    @Override
    public Map build() {
      return new DefaultMapNode(values);
    }
  }

/**
* The default implementation of {@link Entry}.
 * @param key the key
 * @param value the value
*/
  public record DefaultEntry(@Override java.lang.String key, @Override ConfigurationNode value)
      implements Entry {
/**
* Constructs a {@code DefaultMapNode.DefaultEntry}.
 * @param key the key
 * @param value the value
 *              @throws NullPointerException if the key or value are {@code null}.
*/
    public DefaultEntry {
      Objects.requireNonNull(key, "key cannot be null");
      Objects.requireNonNull(value, "value cannot be null");
    }
}
}
