package me.sparky983.warp;

import me.sparky983.warp.internal.StaticRenderer;
import org.jspecify.annotations.Nullable;

public interface Renderer<T extends @Nullable Object> {
  static <T extends @Nullable Object> Renderer<T> of(final T value) {
    return new StaticRenderer<>(value);
  }

  T render(Context context);

  interface Context {
  }
}
