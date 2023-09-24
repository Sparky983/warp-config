package me.sparky983.warp.internal;

import java.util.List;
import java.util.Objects;
import me.sparky983.warp.Renderer;

final class ListRenderer<T> implements Renderer<List<T>> {
  private final List<Renderer<? extends T>> renderers;

  ListRenderer(final List<Renderer<? extends T>> renderers) {
    this.renderers = List.copyOf(renderers);
  }

  @Override
  public List<T> render(final Context context) {
    Objects.requireNonNull(context, "context cannot be null");

    return renderers.stream()
        .<T>map((renderer) -> renderer.render(context))
        .toList();
  }
}
