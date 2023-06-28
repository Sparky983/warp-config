package me.sparky983.warp.internal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.ConfigurationNode;

/** The default implementation of {@link Map}. */
public record DefaultMapNode(@Override java.util.Map<String, ConfigurationNode> values)
    implements ConfigurationNode.Map {
  /**
   * Constructs the map values.
   *
   * @param values the values
   * @throws NullPointerException if the values map is {@code null} or has an entry that contains
   *     {@code null}.
   */
  public DefaultMapNode(final java.util.Map<String, ConfigurationNode> values) {
    this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
  }

  @Override
  public Optional<ConfigurationNode> getValue(final String key) {
    return Optional.ofNullable(values.get(key));
  }

  @Override
  public Set<String> keys() {
    return values.keySet();
  }

  /** The default implementation of {@link Builder}. */
  public static final class DefaultBuilder implements Builder {
    private final java.util.Map<String, ConfigurationNode> values = new LinkedHashMap<>();

    @Override
    public Builder entry(final String key, final ConfigurationNode value) {
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
}
