package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ConfigurationExceptionTest {
  @Test
  void testNew_NullErrors() {
    assertThrows(NullPointerException.class, () -> new ConfigurationException("test", null));
  }

  @Test
  void testNew_NullError() {
    assertThrows(
        NullPointerException.class,
        () -> new ConfigurationException("test", Collections.singleton(null)));
  }

  @Test
  void testNew() {
    final ConfigurationException exception =
        new ConfigurationException(
            "message",
            Set.of(
                ConfigurationError.error("Something went wrong"),
                ConfigurationError.group(
                    "animals.horse",
                    ConfigurationError.error("Invalid horse b"),
                    ConfigurationError.error("Invalid horse a")),
                ConfigurationError.group("version", ConfigurationError.error("Must be a number"))));

    assertEquals(
        """
        message:
         - Something went wrong
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
