package me.sparky983.warp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import me.sparky983.warp.Types;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
class ParameterizedTypeTest {
  @Test
  void testOfRawType_Null() {
    assertThrows(NullPointerException.class, () -> ParameterizedType.of(null));
  }

  @Test
  void testOfRawType() {
    final ParameterizedType<String> type = ParameterizedType.of(String.class);

    assertEquals(String.class, type.rawType());
    assertEquals(List.of(), type.typeArguments());
    assertEquals(List.of(), type.rawTypeArguments());
  }

  @Test
  void testOfParameterizedTypeArguments_NullRawType() {
    assertThrows(
        NullPointerException.class, () -> ParameterizedType.of(null, new ParameterizedType<?>[0]));
  }

  @Test
  void testOfParameterizedTypeArguments_NullTypeArguments() {
    assertThrows(
        NullPointerException.class,
        () -> ParameterizedType.of(String.class, (ParameterizedType<?>[]) null));
  }

  @Test
  void testOfParameterizedTypeArguments_NullTypeArgument() {
    assertThrows(
        NullPointerException.class,
        () -> ParameterizedType.of(String.class, (ParameterizedType<?>) null));
  }

  @Test
  void testOfParameterizedTypeArguments_IncorrectNumberOfTypeArguments() {
    assertThrows(
        IllegalArgumentException.class,
        () -> ParameterizedType.of(Map.class, ParameterizedType.of(String.class)));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            ParameterizedType.of(
                List.class,
                ParameterizedType.of(String.class),
                ParameterizedType.of(String.class)));
    assertThrows(
        IllegalArgumentException.class,
        () -> ParameterizedType.of(String.class, ParameterizedType.of(String.class)));
  }

  @Test
  void testOfParameterizedTypeArguments_RawType() {
    final ParameterizedType<List> type =
        ParameterizedType.of(List.class, new ParameterizedType<?>[0]);

    assertEquals(List.class, type.rawType());
    assertEquals(List.of(), type.typeArguments());
    assertEquals(List.of(), type.rawTypeArguments());
  }

  @Test
  void testOfParameterizedTypeArguments_ParameterizedType() {
    final ParameterizedType<Map> type =
        ParameterizedType.of(
            Map.class, ParameterizedType.of(String.class), ParameterizedType.of(Integer.class));

    assertEquals(Map.class, type.rawType());
    assertEquals(
        List.of(ParameterizedType.of(String.class), ParameterizedType.of(Integer.class)),
        type.typeArguments());
    assertEquals(List.of(String.class, Integer.class), type.rawTypeArguments());
  }

  @Test
  void testOfRawTypeArguments_NullRawType() {
    assertThrows(NullPointerException.class, () -> ParameterizedType.of(null, new Class<?>[0]));
  }

  @Test
  void testOfRawTypeArguments_NullTypeArguments() {
    assertThrows(
        NullPointerException.class, () -> ParameterizedType.of(String.class, (Class<?>[]) null));
  }

  @Test
  void testOfRawTypeArguments_NullTypeArgument() {
    assertThrows(
        NullPointerException.class, () -> ParameterizedType.of(String.class, (Class<?>) null));
  }

  @Test
  void testOfRawTypeArguments_IncorrectNumberOfTypeArguments() {
    assertThrows(
        IllegalArgumentException.class, () -> ParameterizedType.of(Map.class, String.class));
    assertThrows(
        IllegalArgumentException.class,
        () -> ParameterizedType.of(List.class, String.class, String.class));
    assertThrows(
        IllegalArgumentException.class, () -> ParameterizedType.of(String.class, String.class));
  }

  @Test
  void testOfRawTypeArguments_RawType() {
    final ParameterizedType<List> type = ParameterizedType.of(List.class, new Class<?>[0]);

    assertEquals(List.class, type.rawType());
    assertEquals(List.of(), type.typeArguments());
    assertEquals(List.of(), type.rawTypeArguments());
  }

  @Test
  void testOfRawTypeArguments_ParameterizedType() {
    final ParameterizedType<Map> type =
        ParameterizedType.of(Map.class, String.class, Integer.class);

    assertEquals(Map.class, type.rawType());
    assertEquals(
        List.of(ParameterizedType.of(String.class), ParameterizedType.of(Integer.class)),
        type.typeArguments());
    assertEquals(List.of(String.class, Integer.class), type.rawTypeArguments());
  }

  @Test
  void testOfType_Null() {
    assertThrows(NullPointerException.class, () -> ParameterizedType.of((Type) null));
  }

  @Test
  void testOfType_RawType() {
    final ParameterizedType<?> type = ParameterizedType.of((Type) String.class);

    assertEquals(String.class, type.rawType());
    assertEquals(List.of(), type.typeArguments());
    assertEquals(List.of(), type.rawTypeArguments());
  }

  @Test
  void testOfType_ParameterizedType() {
    final ParameterizedType<?> type = ParameterizedType.of(Types.INTEGER_TO_STRING_LIST_MAP);

    assertEquals(Map.class, type.rawType());
    assertEquals(
        List.of(
            ParameterizedType.of(Integer.class), ParameterizedType.of(List.class, String.class)),
        type.typeArguments());
    assertEquals(List.of(Integer.class, List.class), type.rawTypeArguments());
  }

  @Test
  void testOfType_Wildcard() {
    final ParameterizedType<?> type = ParameterizedType.of(Types.UPPER_BOUNDED_CHAR_SEQUENCE_LIST);

    assertEquals(List.class, type.rawType());
    assertEquals(List.of(ParameterizedType.of(CharSequence.class)), type.typeArguments());
    assertEquals(List.of(CharSequence.class), type.rawTypeArguments());
  }

  @Test
  void testOfType_GenericArrayType() {
    final ParameterizedType<?> type = ParameterizedType.of(Types.STRING_LIST_ARRAY);

    assertEquals(List[].class, type.rawType());
    assertEquals(List.of(), type.typeArguments());
    assertEquals(List.of(), type.rawTypeArguments());
  }

  @Test
  void testOfType_TypeVariable() {
    assertThrows(IllegalArgumentException.class, () -> ParameterizedType.of(Types.TYPE_VARIABLE));
  }

  @Test
  void testOfType_UnexpectedType() {
    assertThrows(IllegalArgumentException.class, () -> ParameterizedType.of(new Type() {}));
  }

  @Test
  void testIsRaw_Raw() {
    assertTrue(ParameterizedType.of(String.class).isRaw());
    assertTrue(ParameterizedType.of(String.class, new ParameterizedType<?>[0]).isRaw());
    assertTrue(ParameterizedType.of(String.class, new Class<?>[0]).isRaw());
    assertTrue(ParameterizedType.of((Type) String.class).isRaw());
  }

  @Test
  void testIsRaw_Parameterized() {
    assertFalse(ParameterizedType.of(List.class, ParameterizedType.of(String.class)).isRaw());
    assertFalse(ParameterizedType.of(List.class, String.class).isRaw());
    assertFalse(ParameterizedType.of(Types.STRING_LIST).isRaw());
  }

  @Test
  void testIsParameterized_Raw() {
    assertFalse(ParameterizedType.of(String.class).isParameterized());
    assertFalse(ParameterizedType.of(String.class, new ParameterizedType<?>[0]).isParameterized());
    assertFalse(ParameterizedType.of(String.class, new Class<?>[0]).isParameterized());
    assertFalse(ParameterizedType.of((Type) String.class).isParameterized());
  }

  @Test
  void testIsParameterized_Parameterized() {
    assertTrue(
        ParameterizedType.of(List.class, ParameterizedType.of(String.class)).isParameterized());
    assertTrue(ParameterizedType.of(List.class, String.class).isParameterized());
    assertTrue(ParameterizedType.of(Types.STRING_LIST).isParameterized());
  }

  @Test
  void testEquals_Null() {
    assertNotEquals(ParameterizedType.of(String.class), null);
  }

  @Test
  void testEquals_WrongType() {
    assertNotEquals(ParameterizedType.of(String.class), new Object());
  }

  @Test
  void testEquals_DifferentRawType() {
    assertNotEquals(ParameterizedType.of(String.class), ParameterizedType.of(Integer.class));
  }

  @Test
  void testEquals_DifferentTypeArguments() {
    assertNotEquals(
        ParameterizedType.of(List.class), ParameterizedType.of(List.class, String.class));
  }

  @Test
  void testEquals_Same() {
    final ParameterizedType<String> type = ParameterizedType.of(String.class);

    assertEquals(type, type);
  }

  @Test
  void testEquals_Equal() {
    assertEquals(
        ParameterizedType.of(List.class, String.class),
        ParameterizedType.of(List.class, String.class));
  }

  // No tests for different hash codes as that cannot be not guaranteed.

  @Test
  void testHashCode_Equal() {
    assertEquals(
        ParameterizedType.of(List.class, String.class).hashCode(),
        ParameterizedType.of(List.class, String.class).hashCode());
  }

  @Test
  void testToString_Raw() {
    assertEquals("java.lang.String", ParameterizedType.of(String.class).toString());
  }

  @Test
  void testToString_Parameterized() {
    assertEquals(
        "java.util.List<java.lang.String>",
        ParameterizedType.of(List.class, String.class).toString());
    assertEquals(
        "java.util.Map<java.lang.Integer, java.util.List<java.lang.String>>",
        ParameterizedType.of(
                Map.class,
                ParameterizedType.of(Integer.class),
                ParameterizedType.of(List.class, String.class))
            .toString());
  }
}
