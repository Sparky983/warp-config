package me.sparky983.warp;

import java.util.List;
import java.util.Optional;
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
  public interface StringList {
    @Property("property")
    List<java.lang.String> property();
  }

  @Configuration
  public interface NonDeserializableList {
    @Property("property")
    List<Random> property();
  }

  @SuppressWarnings("rawtypes")
  @Configuration
  public interface RawList {
    @Property("property")
    List property();
  }

  @Configuration
  public interface StringStringMap {
    @Property("property")
    java.util.Map<java.lang.String, java.lang.String> property();
  }

  @Configuration
  public interface NonDeserializableKeyMap {
    @Property("property")
    java.util.Map<Random, java.lang.String> property();
  }

  @Configuration
  public interface NonDeserializableValueMap {
    @Property("property")
    java.util.Map<java.lang.String, Random> property();
  }

  @SuppressWarnings("rawtypes")
  @Configuration
  public interface RawMap {
    @Property("property")
    java.util.Map property();
  }

  @Configuration
  public interface StringOptional {
    @Property("property")
    Optional<java.lang.String> property();
  }

  @Configuration
  public interface NonDeserializableOptional {
    @Property("property")
    Optional<Random> property();
  }

  @SuppressWarnings("rawtypes")
  @Configuration
  public interface RawOptional {
    @Property("property")
    Optional property();
  }

  @Configuration
  public interface Nested {
    @Property("nested.property")
    java.lang.String property();
  }

  @Configuration
  public interface NonDeserializable {
    @Property("property")
    Random property();
  }

  @Configuration
  public static class Class {}
}
