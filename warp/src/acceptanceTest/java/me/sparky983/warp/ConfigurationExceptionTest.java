package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ConfigurationExceptionTest {
  @Test
  void testNewCollection_NullErrors() {
    assertThrows(
        NullPointerException.class,
        () -> new ConfigurationException((Collection<? extends ConfigurationError>) null));
  }

  @Test
  void testNewCollection_NullError() {
    assertThrows(
        NullPointerException.class, () -> new ConfigurationException(Collections.singleton(null)));
  }

  @Test
  void testNewVarargs_NullErrors() {
    assertThrows(
        NullPointerException.class, () -> new ConfigurationException((ConfigurationError[]) null));
  }

  @Test
  void testNewVarargs_NullError() {
    assertThrows(
        NullPointerException.class,
        () -> new ConfigurationException(new ConfigurationError[] {null}));
  }

  @Test
  void testErrorCollection() {
    final ConfigurationException exception =
        new ConfigurationException(
            List.of(
                ConfigurationError.group(
                    "animals.horse",
                    ConfigurationError.error("Invalid horse a"),
                    ConfigurationError.error("Invalid horse b")),
                ConfigurationError.group("version", ConfigurationError.error("Must be a number")),
                ConfigurationError.error("Something went wrong")));

    assertEquals("""
        - Something went wrong
        - animals.horse:
          - Invalid horse a
          - Invalid horse b
        - version:
          - Must be a number
        """.indent(1).stripTrailing(),
        exception.getMessage());
    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "animals.horse",
                ConfigurationError.error("Invalid horse a"),
                ConfigurationError.error("Invalid horse b")),
            ConfigurationError.group("version", ConfigurationError.error("Must be a number")),
            ConfigurationError.error("Something went wrong")),
        exception.errors());
  }

  @Test
  void testErrorVarargs() {
    final ConfigurationException exception =
        new ConfigurationException(
            ConfigurationError.group(
                "animals.horse",
                ConfigurationError.error("Invalid horse a"),
                ConfigurationError.error("Invalid horse b")),
            ConfigurationError.group("version", ConfigurationError.error("Must be a number")),
            ConfigurationError.error("Something went wrong"));

    final Collection<ConfigurationError> errors = exception.errors();

    assertEquals("""
        - Something went wrong
        - animals.horse:
          - Invalid horse a
          - Invalid horse b
        - version:
          - Must be a number
        """.indent(1).stripTrailing(),
        exception.getMessage());
    assertIterableEquals(
        List.of(
            ConfigurationError.group(
                "animals.horse",
                ConfigurationError.error("Invalid horse a"),
                ConfigurationError.error("Invalid horse b")),
            ConfigurationError.group("version", ConfigurationError.error("Must be a number")),
            ConfigurationError.error("Something went wrong")),
        errors);
    assertThrows(UnsupportedOperationException.class, () -> errors.add(ConfigurationError.error("some error")));
  }
}
