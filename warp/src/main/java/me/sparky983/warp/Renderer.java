package me.sparky983.warp;

import java.util.Objects;

/**
 * Renders a value.
 *
 * <p>This method is called every time a value is requested.
 *
 * @param <T> the type of the value
 * @since 0.1
 */
public interface Renderer<T> {
  /**
   * Creates a renderer that always returns the given value.
   *
   * @param value the value
   * @param <T> the type of the value
   * @return the renderer
   * @throws NullPointerException if the value is {@code null}.
   * @since 0.1
   */
  static <T> Renderer<T> of(final T value) {
    Objects.requireNonNull(value, "value cannot be null");

    return (context) -> {
      Objects.requireNonNull(context, "context cannot be null");

      return value;
    };
  }

  /**
   * Renders the value.
   *
   * @param context the context; never {@code null}
   * @return the rendered value
   * @since 0.1
   */
  T render(Context context);

  /**
   * The context for rendering.
   *
   * @since 0.1
   */
  interface Context {}
}
