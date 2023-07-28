package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
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
            new LinkedHashSet<>(
                Arrays.asList(ConfigurationError.of("error 1"), ConfigurationError.of("error 2"))));

    assertEquals(
        """
        message:
         - error 1
         - error 2""", exception.getMessage());
    assertEquals(
        Set.of(ConfigurationError.of("error 1"), ConfigurationError.of("error 2")),
        exception.errors());
  }
}
