package me.sparky983.warp.internal;

import java.util.Objects;
import me.sparky983.warp.Renderer;
import org.jspecify.annotations.Nullable;

public final class StaticRenderer<T extends @Nullable Object> implements Renderer<T> {
  private final T value;

  public StaticRenderer(final T value) {
    Objects.requireNonNull(value, "value cannot be null");

    this.value = value;
  }

  @Override
  public @Nullable T render(final Context context) {
    Objects.requireNonNull(context, "context cannot be null");

    return value;
  }
}
