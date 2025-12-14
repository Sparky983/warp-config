package me.sparky983.warp.adventure;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Placeholder {
  String value();

  @Documented
  @Retention(RUNTIME)
  @Target(PARAMETER)
  @interface Parsed {
    String value();
  }
}
