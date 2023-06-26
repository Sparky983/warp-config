package me.sparky983.warp.internal;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import me.sparky983.warp.ConfigurationValue;

/** The default implementation of {@link Map}. */
public record DefaultMapValue(@Override java.util.Map<String, ConfigurationValue> values)
    implements ConfigurationValue.Map {
  /**
   * Constructs the map values.
   *
   * @param values the values
   * @throws NullPointerException if the values map is {@code null} or has an entry that contains
   *     {@code null}.
   */
  public DefaultMapValue(final java.util.Map<String, ConfigurationValue> values) {
    this.values = java.util.Map.copyOf(values);
  }

  @Override
  public Optional<ConfigurationValue> getValue(final String key) {
    return Optional.ofNullable(values.get(key));
  }

  @Override
  public Set<String> keys() {
    return values.keySet();
  }

  /** The default implementation of {@link Builder}. */
  public static final class DefaultBuilder implements Builder {
    private final java.util.Map<String, ConfigurationValue> values = new HashMap<>();

    @Override
    public Builder entry(final String key, final ConfigurationValue value) {
      Objects.requireNonNull(key, "key cannot be null");
      Objects.requireNonNull(value, "value cannot be null");
      values.put(key, value);
      return this;
    }

    @Override
    public Map build() {
      return new DefaultMapValue(values);
    }
  }
}
