package me.sparky983.warp;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class Types {
  public static final Type STRING_LIST;
  public static final Type INTEGER_TO_STRING_LIST_MAP;
  public static final Type UPPER_BOUNDED_CHAR_SEQUENCE_LIST;
  public static final Type STRING_LIST_ARRAY;
  public static final Type TYPE_VARIABLE;

  static {
    try {
      STRING_LIST =
          Types.class.getDeclaredMethod("stringList", List.class).getGenericParameterTypes()[0];
      INTEGER_TO_STRING_LIST_MAP =
          Types.class.getDeclaredMethod("integerToStringListMap", Map.class)
              .getGenericParameterTypes()[0];
      UPPER_BOUNDED_CHAR_SEQUENCE_LIST =
          Types.class.getDeclaredMethod("upperBoundedStringList", List.class)
              .getGenericParameterTypes()[0];
      STRING_LIST_ARRAY =
          Types.class.getDeclaredMethod("stringListArray", List[].class)
              .getGenericParameterTypes()[0];
      TYPE_VARIABLE =
          Types.class.getDeclaredMethod("typeVariable", Object.class).getGenericParameterTypes()[0];
    } catch (final NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private Types() {}

  private static void stringList(final List<String> list) {}

  private static void integerToStringListMap(final Map<Integer, List<String>> list) {}

  private static void upperBoundedStringList(final List<? extends CharSequence> list) {}

  private static void stringListArray(final List<String>[] list) {}

  private static <T> void typeVariable(final T t) {}
}
