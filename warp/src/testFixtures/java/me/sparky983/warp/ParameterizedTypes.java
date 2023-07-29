package me.sparky983.warp;

import me.sparky983.warp.internal.ParameterizedType;

public final class ParameterizedTypes {
  public static final ParameterizedType<Byte> BYTE = ParameterizedType.of(byte.class);
  public static final ParameterizedType<Short> SHORT = ParameterizedType.of(short.class);
  public static final ParameterizedType<Integer> INTEGER = ParameterizedType.of(int.class);
  public static final ParameterizedType<Long> LONG = ParameterizedType.of(long.class);
  public static final ParameterizedType<Float> FLOAT = ParameterizedType.of(float.class);
  public static final ParameterizedType<Double> DOUBLE = ParameterizedType.of(double.class);
  public static final ParameterizedType<Boolean> BOOLEAN = ParameterizedType.of(boolean.class);

  private ParameterizedTypes() {}
}
