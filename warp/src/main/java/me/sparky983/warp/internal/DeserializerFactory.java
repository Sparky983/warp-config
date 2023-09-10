package me.sparky983.warp.internal;

@FunctionalInterface
public interface DeserializerFactory<T> {
  Deserializer<? extends T> create(
      DeserializerRegistry registry, ParameterizedType<? extends T> type);
}
