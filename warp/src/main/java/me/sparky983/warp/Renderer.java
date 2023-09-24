package me.sparky983.warp;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

public interface Renderer<T extends @Nullable Object> {
  static <T extends @Nullable Object> Renderer<T> of(final T value) {
    Objects.requireNonNull(value, "value cannot be null");

    return (context) -> {
      Objects.requireNonNull(context, "context cannot be null");

      return value;
    };
  }

  T render(Context context);

  interface Context {
  }
}
