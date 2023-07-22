package me.sparky983.warp;

import java.util.Random;
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.annotations.Property;

public interface Configurations {
  interface Invalid {}

  @Configuration
  interface Empty {}

  @Configuration
  interface Int {
    @Property("property")
    int property();
  }

  @Configuration
  interface String {
    @Property("property")
    java.lang.String property();
  }

  @Configuration
  interface Unserializable {
    @Property("property")
    Random property();
  }
}
