package me.sparky983.warp;

import java.util.Random;
import me.sparky983.warp.annotations.Configuration;
import me.sparky983.warp.annotations.Property;

public final class Configurations {
  private Configurations() {}

  public interface MissingAnnotation {}

  @Configuration
  interface Private {}

  @Configuration
  public interface Hidden {}

  @Configuration
  public sealed interface Sealed {}

  /** Sealed classes must permit at least 1 subclass. */
  @Configuration
  private non-sealed interface Permitted extends Sealed {}

  @Configuration
  public interface Generic<T> {}

  @Configuration
  public interface NonProperty {
    void nonProperty();
  }

  @Configuration
  public interface StaticProperty {
    @Property("property")
    static int property() {
      return 0;
    }
  }

  @Configuration
  public interface GenericProperty {
    @Property("property")
    <T> int property();
  }

  @Configuration
  public interface ParameterizedProperty {
    @Property("property")
    int property(int parameter);
  }

  @Configuration
  public interface Empty {}

  @Configuration
  public interface Byte {
    @Property("property")
    byte property();
  }

  @Configuration
  public interface Short {
    @Property("property")
    short property();
  }

  @Configuration
  public interface Integer {
    @Property("property")
    int property();
  }

  @Configuration
  public interface Long {
    @Property("property")
    long property();
  }

  @Configuration
  public interface Float {
    @Property("property")
    float property();
  }

  @Configuration
  public interface Double {
    @Property("property")
    double property();
  }

  @Configuration
  public interface Boolean {
    @Property("property")
    boolean property();
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

  @Configuration
  public static class Class {}
}
