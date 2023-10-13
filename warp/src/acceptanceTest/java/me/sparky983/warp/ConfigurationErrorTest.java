package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ConfigurationErrorTest {
  @Nested
  class Group {
    @Test
    void testGroupCollection_NullName() {
      assertThrows(NullPointerException.class, () -> ConfigurationError.group(null, List.of()));
    }

    @Test
    void testGroupCollection_NullErrors() {
      assertThrows(
          NullPointerException.class,
          () -> ConfigurationError.group("name", (Collection<ConfigurationError>) null));
    }

    @Test
    void testGroupCollection_NullError() {
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
    void testName_Collection() {
      final ConfigurationError.Group group = ConfigurationError.group("name", List.of());

      assertEquals("name", group.name());
    }

    @Test
    void testName_Varargs() {
      final ConfigurationError.Group group = ConfigurationError.group("name");

      assertEquals("name", group.name());
    }

    @Test
    void testErrors_Collection() {
      final ConfigurationError.Group group =
          ConfigurationError.group(
              "name",
              List.of(ConfigurationError.group("group"), ConfigurationError.error("message")));

      assertIterableEquals(
          List.of(ConfigurationError.error("message"), ConfigurationError.group("group")),
          group.errors());
    }

    @Test
    void testErrors_Varargs() {
      final ConfigurationError.Group group =
          ConfigurationError.group(
              "name", ConfigurationError.group("group"), ConfigurationError.error("message"));

      assertIterableEquals(
          List.of(ConfigurationError.error("message"), ConfigurationError.group("group")),
          group.errors());
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
  }
}
