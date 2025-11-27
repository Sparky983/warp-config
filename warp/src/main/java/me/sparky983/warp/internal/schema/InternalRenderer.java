package me.sparky983.warp.internal.schema;

import me.sparky983.warp.Renderer;

/**
 * An internal version of {@link Renderer} that allows for private access to additional context such
 * as the proxy that the renderer was called on.
 *
 * @param <T> the type of the value
 */
public interface InternalRenderer<T> {
  /**
   * Renders the value.
   *
   * @param proxy the proxy the renderer was called on
   * @param context the context
   * @return the rendered value
   * @throws Throwable if there was an exception; may only be thrown by user code.
   */
  T render(Object proxy, Renderer.Context context) throws Throwable;
}
