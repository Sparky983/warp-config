package me.sparky983.warp.internal;

import me.sparky983.warp.Deserializer;

@FunctionalInterface
public interface DeserializerFactory<T> {
  Deserializer<? extends T> create(
      DeserializerRegistry registry, ParameterizedType<? extends T> type);
}
