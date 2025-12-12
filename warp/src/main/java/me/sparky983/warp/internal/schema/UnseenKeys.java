package me.sparky983.warp.internal.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import org.jetbrains.annotations.VisibleForTesting;
import org.jspecify.annotations.Nullable;

/**
 * Record to represent keys that have not been read yet allowing for nested key checks. {@code null}
 * values represent a non-map value.
 */
final class UnseenKeys {
  private final Map<String, @Nullable UnseenKeys> unseenKeys;

  private UnseenKeys(final Map<String, @Nullable UnseenKeys> unseenKeys) {
    this.unseenKeys = unseenKeys;
  }

  static UnseenKeys createInitialKeys(
      final Map<String, ? extends ConfigurationNode> configurationMap) {
    Objects.requireNonNull(configurationMap, "configurationMap cannot be null");

    final Map<String, @Nullable UnseenKeys> unknownKeys = new LinkedHashMap<>();
    configurationMap.forEach(
        (key, value) -> {
          try {
            unknownKeys.put(key, createInitialKeys(value.asMap()));
          } catch (final DeserializationException e) {
            unknownKeys.put(key, null);
          }
        });
    return new UnseenKeys(unknownKeys);
  }

  void remove(final List<String> keys) {
    if (keys.isEmpty()) {
      throw new IllegalArgumentException("keys cannot be empty");
    }
    remove0(keys, 0);
  }

  private void remove0(final List<String> keys, final int index) {
    final String key = keys.get(index);
    if (index == keys.size() - 1) {
      this.unseenKeys.remove(key);
    } else {
      final UnseenKeys children = this.unseenKeys.get(key);
      if (children != null) {
        children.remove0(keys, index + 1);
      }
    }
  }

  @VisibleForTesting
  Map<String, @Nullable UnseenKeys> unseenKeys() {
    return Collections.unmodifiableMap(this.unseenKeys);
  }

  List<ConfigurationError> makeErrors() {
    final List<ConfigurationError> errors = new ArrayList<>();
    makeErrors0(errors);
    return errors;
  }

  private void makeErrors0(final List<? super ConfigurationError> errors) {
    this.unseenKeys.forEach(
        (key, value) -> {
          if (value == null) {
            errors.add(ConfigurationError.group(key, ConfigurationError.error("Unknown property")));
          } else {
            final List<ConfigurationError> childErrors = new ArrayList<>();
            value.makeErrors0(childErrors);
            if (!childErrors.isEmpty()) {
              errors.add(ConfigurationError.group(key, childErrors));
            }
          }
        });
  }
}
