package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ConfigurationErrorTest {
  @Nested
  class Group {
    @Test
    void testGroupSet_NullName() {
      assertThrows(NullPointerException.class, () -> ConfigurationError.group(null, Set.of()));
    }

    @Test
    void testGroupSet_NullErrors() {
      assertThrows(
          NullPointerException.class,
          () -> ConfigurationError.group("name", (Set<ConfigurationError>) null));
    }

    @Test
    void testGroupSet_NullError() {
      assertThrows(
          NullPointerException.class,
          () -> ConfigurationError.group("name", Collections.singleton(null)));
    }

    @Test
    void testGroupVarargs_NullName() {
      assertThrows(
          NullPointerException.class,
          () -> ConfigurationError.group(null, new ConfigurationError[0]));
    }

    @Test
    void testGroupVarargs_NullErrors() {
      assertThrows(
          NullPointerException.class,
          () -> ConfigurationError.group("name", (ConfigurationError[]) null));
    }

    @Test
    void testGroupVarargs_NullError() {
      assertThrows(
          NullPointerException.class,
          () -> ConfigurationError.group("name", new ConfigurationError[] {null}));
    }

    @Test
    void testGroupVarargs_DuplicateError() {
      assertThrows(
          IllegalArgumentException.class,
          () ->
              ConfigurationError.group(
                  "name",
                  ConfigurationError.error("message"),
                  ConfigurationError.error("message")));
    }

    @Test
    void testName_Set() {
      final ConfigurationError.Group group = ConfigurationError.group("name", Set.of());

      assertEquals("name", group.name());
    }

    @Test
    void testName_Varargs() {
      final ConfigurationError.Group group = ConfigurationError.group("name");

      assertEquals("name", group.name());
    }

    @Test
    void testErrors_Set() {
      final ConfigurationError.Group group =
          ConfigurationError.group(
              "name",
              Set.of(ConfigurationError.error("message b"), ConfigurationError.error("message a")));

      assertIterableEquals(
          List.of(ConfigurationError.error("message a"), ConfigurationError.error("message b")),
          group.errors());
    }

    @Test
    void testErrors_Varargs() {
      final ConfigurationError.Group group =
          ConfigurationError.group(
              "name", ConfigurationError.error("message b"), ConfigurationError.error("message a"));

      assertIterableEquals(
          List.of(ConfigurationError.error("message a"), ConfigurationError.error("message b")),
          group.errors());
    }

    @Test
    void testCompareTo_SetError() {
      final ConfigurationError.Group group = ConfigurationError.group("name", Set.of());

      assertEquals(1, group.compareTo(ConfigurationError.error("message")));
    }

    @Test
    void testCompareTo_SetGroupGreaterThan() {
      final ConfigurationError.Group group = ConfigurationError.group("name a", Set.of());

      assertEquals(-1, group.compareTo(ConfigurationError.group("name b", Set.of())));
    }

    @Test
    void testCompareTo_SetGroupLessThan() {
      final ConfigurationError.Group group = ConfigurationError.group("name b", Set.of());

      assertEquals(1, group.compareTo(ConfigurationError.group("name a", Set.of())));
    }

    @Test
    void testCompareTo_SetGroupEquals() {
      final ConfigurationError.Group group = ConfigurationError.group("name", Set.of());

      assertEquals(0, group.compareTo(ConfigurationError.group("name", Set.of())));
    }
  }

  @Nested
  class Error {
    @Test
    void testError_Null() {
      assertThrows(NullPointerException.class, () -> ConfigurationError.error(null));
    }

    @Test
    void testMessage() {
      final ConfigurationError.Error error = ConfigurationError.error("message");

      assertEquals("message", error.message());
    }

    @Test
    void testCompareTo_ErrorGreaterThan() {
      final ConfigurationError.Error error = ConfigurationError.error("message a");

      assertEquals(-1, error.compareTo(ConfigurationError.error("message b")));
    }

    @Test
    void testCompareTo_ErrorLessThan() {
      final ConfigurationError.Error error = ConfigurationError.error("message b");

      assertEquals(1, error.compareTo(ConfigurationError.error("message a")));
    }

    @Test
    void testCompareTo_ErrorEquals() {
      final ConfigurationError.Error error = ConfigurationError.error("message");

      assertEquals(0, error.compareTo(ConfigurationError.error("message")));
    }

    @Test
    void testCompareTo_Group() {
      final ConfigurationError.Error error = ConfigurationError.error("message");

      assertEquals(-1, error.compareTo(ConfigurationError.group("name", Set.of())));
    }
  }
}
