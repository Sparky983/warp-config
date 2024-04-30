package me.sparky983.warp.yaml;

import java.util.Map;
import me.sparky983.warp.ConfigurationException;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.ConfigurationSource;
import me.sparky983.warp.DeserializationException;

public final class ConfigurationNodes {
  private ConfigurationNodes() {}

  public static boolean sourceIsMix(final ConfigurationSource source)
      throws ConfigurationException {
    return source.configuration().map(ConfigurationNodes::nodeIsMix).orElse(false);
  }

  /**
   * An exhaustive check to see if the given node matches the general {@code MIX} input used in YAML
   * tests.
   *
   * <p>Using this method is hard to debug and was annoying to write, but this should be a one-off
   * thing because of YAML's typelessness.
   *
   * @param node the nodes
   * @return {@code true} if they match, otherwise {@code false}
   */
  public static boolean nodeIsMix(final ConfigurationNode node) {
    // @spotless:off
    final Map<String, ConfigurationNode> values;
    try {
      values = node.asMap();
    } catch (final DeserializationException exception) {
      return false;
    }
    final ConfigurationNode noValue = values.get("no value");
    final ConfigurationNode nil = values.get("null");
    final ConfigurationNode boolTrue = values.get("true");
    final ConfigurationNode boolFalse = values.get("false");
    final ConfigurationNode integer = values.get("integer");
    final ConfigurationNode decimal = values.get("decimal");
    final ConfigurationNode string = values.get("string");

    final ConfigurationNode list = values.get("list");
    final ConfigurationNode firstListElement;
    final ConfigurationNode secondListElement;
    if (list == null) {
      firstListElement = null;
      secondListElement = null;
    } else {
      try {
        firstListElement = list.asList().get(0);
        secondListElement = list.asList().get(1);
      } catch (final DeserializationException exception) {
        return false;
      }
    }

    final ConfigurationNode map = values.get("map");
    final ConfigurationNode value;
    if (map == null) {
      value = null;
    } else {
      try {
        value = map.asMap().get("key");
      } catch (final DeserializationException exception) {
        return false;
      }
    }

    try {
      return values.size() == 9
          && noValue != null
          && nil != null
          && boolTrue != null
          && boolFalse != null
          && integer != null
          && decimal != null
          && string != null
          && list != null
          && list.asList().size() == 2
          && map != null
          && map.asMap().size() == 1
          && value != null
          && noValue.isNil()
          && nil.asString().equals("null")
          && cannotConvert(noValue::asDecimal, noValue::asInteger, noValue::asBoolean, noValue::asList, noValue::asMap)
          && nil.isNil()
          && nil.asString().equals("null")
          && cannotConvert(nil::asDecimal, nil::asInteger, nil::asBoolean, nil::asList, nil::asMap)
          && boolTrue.asBoolean()
          && boolTrue.asString().equals("true")
          && !boolTrue.isNil()
          && cannotConvert(boolTrue::asDecimal, boolTrue::asInteger, boolTrue::asList, boolTrue::asMap)
          && !boolFalse.asBoolean()
          && boolFalse.asString().equals("false")
          && !boolFalse.isNil()
          && cannotConvert(boolFalse::asDecimal, boolFalse::asInteger, boolFalse::asList, boolFalse::asMap)
          && integer.asInteger() == 10
          && integer.asDecimal() == 10.0
          && integer.asString().equals("10")
          && !integer.isNil()
          && cannotConvert(integer::asBoolean, integer::asList, integer::asMap)
          && decimal.asDecimal() == 10.0
          && decimal.asString().equals("10.0")
          && !decimal.isNil()
          && cannotConvert(decimal::asInteger, decimal::asBoolean, decimal::asList, decimal::asMap)
          && string.asString().equals("some string")
          && !string.isNil()
          && cannotConvert(string::asDecimal, string::asInteger, string::asBoolean, string::asList, string::asMap)
          && firstListElement.asInteger() == 10
          && firstListElement.asDecimal() == 10.0
          && firstListElement.asString().equals("10")
          && !firstListElement.isNil()
          && cannotConvert(firstListElement::asBoolean, firstListElement::asList, firstListElement::asMap)
          && secondListElement.asString().equals("some string")
          && !secondListElement.isNil()
          && cannotConvert(secondListElement::asDecimal, secondListElement::asInteger, secondListElement::asBoolean, secondListElement::asList, secondListElement::asMap)
          && value.asString().equals("value")
          && !value.isNil()
          && cannotConvert(value::asDecimal, value::asInteger, value::asBoolean, value::asList, noValue::asMap);
    } catch (final DeserializationException exception) {
      return false;
    }
    // @spotless:on
  }

  /**
   * Ensures the given converters all fail.
   *
   * @param converters the converters
   * @return {@code true} if they all fail, otherwise {@code false}
   */
  private static boolean cannotConvert(final Converter... converters) {
    for (final Converter converter : converters) {
      try {
        converter.convert();
        return false;
      } catch (final DeserializationException ignored) {
        // keep going
      }
    }
    return true;
  }

  interface Converter {
    void convert() throws DeserializationException;
  }
}
