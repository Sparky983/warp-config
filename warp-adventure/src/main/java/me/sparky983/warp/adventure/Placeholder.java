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
  // String, ComponentLike, primitive number, char, boolean
  String value();

  // number primitive or boolean
  @Documented
  @Retention(RUNTIME)
  @Target(PARAMETER)
  @interface Choice {
    String value();
  }

  // number, TemporalAccessor
  @Documented
  @Retention(RUNTIME)
  @Target(PARAMETER)
  @interface Format {
    String value();
  }

  // string
  @Documented
  @Retention(RUNTIME)
  @Target(PARAMETER)
  @interface Parsed {
    String value();
  }
}
