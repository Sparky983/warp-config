package me.sparky983.warp.internal.node;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.sparky983.warp.ConfigurationNode;

/**
 * The default {@link Map} implementation of {@link ConfigurationNode}.
 *
 * @param map the map
 */
public record DefaultMapNode(Map<String, ConfigurationNode> map) implements ConfigurationNode {
  /** A reusable empty instance. */
  public static final DefaultMapNode EMPTY = new DefaultMapNode(Map.of());

  /**
   * Constructs a {@code DefaultMapNode}.
   *
   * @param map the map; changes in this map will not be reflected in the constructed {@code
   *     DefaultMapNode}
   * @throws NullPointerException if the map are {@code null} or one of its entries contain {@code
   *     null}
   */
  public DefaultMapNode {
    if (map.isEmpty()) {
      map = Map.of();
    } else {
      map = Collections.unmodifiableMap(new LinkedHashMap<>(map));
      map.forEach(
          (key, value) -> {
            Objects.requireNonNull(key, "map cannot have null key");
            if (value == null) {
              throw new NullPointerException("map[" + key + "] cannot be null");
            }
          });
    }
  }

  @Override
  public Map<String, ConfigurationNode> asMap() {
    return map;
  }

  @Override
  public String toString() {
    return map.entrySet().stream()
        .map((entry) -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining(", ", "{", "}"));
  }
}
