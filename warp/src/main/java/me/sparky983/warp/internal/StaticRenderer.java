package me.sparky983.warp.internal;

import java.util.Objects;
import me.sparky983.warp.Renderer;
import org.jspecify.annotations.Nullable;

public record StaticRenderer<T extends @Nullable Object>(T value) implements Renderer<T> {
  @Override
  public @Nullable T render(final Context context) {
    Objects.requireNonNull(context, "context cannot be null");

    return value;
  }
}
