package me.sparky983.warp;

import java.util.Random;
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.annotations.Property;

public final class Configurations {
  private Configurations() {}

  public interface Invalid {}

  @Configuration
  public interface Empty {}

  @Configuration
  public interface Int {
    @Property("property")
    int property();
  }

  @Configuration
  public interface String {
    @Property("property")
    java.lang.String property();
  }

  @Configuration
  public interface Unserializable {
    @Property("property")
    Random property();
  }
}
