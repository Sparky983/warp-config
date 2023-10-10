package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConfigurationExceptionTest {
  @Test
  void testNewSet_NullErrors() {
    assertThrows(NullPointerException.class, () -> new ConfigurationException(
        (Set<? extends ConfigurationError>) null));
  }

  @Test
  void testNewSet_NullError() {
    assertThrows(
        NullPointerException.class,
        () -> new ConfigurationException(Collections.singleton(null)));
  }

  @Test
  void testNewVarargs_NullErrors() {
    assertThrows(NullPointerException.class, () -> new ConfigurationException((ConfigurationError[]) null));
  }

  @Test
  void testNewVarargs_NullError() {
    assertThrows(
        NullPointerException.class,
        () -> new ConfigurationException(new ConfigurationError[] { null }));
  }

  @Test
  void testErrorSet() {
    final ConfigurationException exception =
        new ConfigurationException(
            Set.of(
                ConfigurationError.error("Something went wrong"),
                ConfigurationError.group(
                    "animals.horse",
                    ConfigurationError.error("Invalid horse b"),
                    ConfigurationError.error("Invalid horse a")),
                ConfigurationError.group("version", ConfigurationError.error("Must be a number"))));

    assertEquals(
        """
        \s- Something went wrong
         - animals.horse:
           - Invalid horse a
           - Invalid horse b
         - version:
           - Must be a number""",
        exception.getMessage());
    assertEquals(
        Set.of(
            ConfigurationError.error("Something went wrong"),
            ConfigurationError.group(
                "animals.horse",
                ConfigurationError.error("Invalid horse b"),
                ConfigurationError.error("Invalid horse a")),
            ConfigurationError.group("version", ConfigurationError.error("Must be a number"))),
        exception.errors());
  }

  @Test
  void testErrorVarargs() {
    final ConfigurationException exception =
        new ConfigurationException(
            ConfigurationError.error("Something went wrong"),
            ConfigurationError.group(
                "animals.horse",
                ConfigurationError.error("Invalid horse b"),
                ConfigurationError.error("Invalid horse a")),
            ConfigurationError.group("version", ConfigurationError.error("Must be a number")));

    assertEquals(
        """
        \s- Something went wrong
         - animals.horse:
           - Invalid horse a
           - Invalid horse b
         - version:
           - Must be a number""",
        exception.getMessage());
    assertEquals(
        Set.of(
            ConfigurationError.error("Something went wrong"),
            ConfigurationError.group(
                "animals.horse",
                ConfigurationError.error("Invalid horse b"),
                ConfigurationError.error("Invalid horse a")),
            ConfigurationError.group("version", ConfigurationError.error("Must be a number"))),
        exception.errors());
  }
}
