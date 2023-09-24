package me.sparky983.warp.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.sparky983.warp.Renderer;

final class MapRenderer<K, V> implements Renderer<Map<K, V>> {
  private final Map<Renderer<? extends K>, Renderer<? extends V>> renderers;

  MapRenderer(final Map<Renderer<? extends K>, Renderer<? extends V>> renderers) {
    this.renderers = Map.copyOf(renderers);
  }

  @Override
  public Map<K, V> render(final Context context) {
    final Map<K, V> values = new HashMap<>();
    renderers.forEach((key, value) -> values.put(key.render(context), value.render(context)));
    return Collections.unmodifiableMap(values);
  }
}
