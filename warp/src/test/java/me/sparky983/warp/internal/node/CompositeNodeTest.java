package me.sparky983.warp.internal.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.DeserializationException;
import org.junit.jupiter.api.Test;

class CompositeNodeTest {
  @Test
  void testAsString_FirstString() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.string("first");
    final ConfigurationNode second = ConfigurationNode.string("second");
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final String value = composite.asString();

    assertEquals("first", value);
  }

  @Test
  void testAsString_SecondString() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.nil();
    final ConfigurationNode second = ConfigurationNode.string("second");
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final String value = composite.asString();

    assertEquals("second", value);
  }

  @Test
  void testAsString_NoneString() {
    final ConfigurationNode composite =
        new CompositeNode(List.of(ConfigurationNode.nil(), ConfigurationNode.nil()));

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, composite::asString);

    assertIterableEquals(List.of(ConfigurationError.error("Must be a string")), thrown.errors());
  }

  @Test
  void testAsDecimal_FirstDecimal() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.decimal(1.0);
    final ConfigurationNode second = ConfigurationNode.decimal(2.0);
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final double value = composite.asDecimal();

    assertEquals(1.0, value);
  }

  @Test
  void testAsDecimal_SecondDecimal() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.string("first");
    final ConfigurationNode second = ConfigurationNode.decimal(2.0);
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final double value = composite.asDecimal();

    assertEquals(2.0, value);
  }

  @Test
  void testAsDecimal_NoneDecimal() {
    final ConfigurationNode nonDecimal = ConfigurationNode.string("non-decimal");
    final ConfigurationNode composite = new CompositeNode(List.of(nonDecimal, nonDecimal));

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, composite::asDecimal);

    assertIterableEquals(List.of(ConfigurationError.error("Must be a decimal")), thrown.errors());
  }

  @Test
  void testAsInteger_FirstInteger() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.integer(1);
    final ConfigurationNode second = ConfigurationNode.integer(2);
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final long value = composite.asInteger();

    assertEquals(1.0, value);
  }

  @Test
  void testAsInteger_SecondInteger() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.string("first");
    final ConfigurationNode second = ConfigurationNode.integer(2);
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final long value = composite.asInteger();

    assertEquals(2.0, value);
  }

  @Test
  void testAsInteger_NoneInteger() {
    final ConfigurationNode nonInteger = ConfigurationNode.string("non-integer");
    final ConfigurationNode composite = new CompositeNode(List.of(nonInteger, nonInteger));

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, composite::asInteger);

    assertIterableEquals(List.of(ConfigurationError.error("Must be an integer")), thrown.errors());
  }

  @Test
  void testAsBoolean_FirstBoolean() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.bool(true);
    final ConfigurationNode second = ConfigurationNode.bool(false);
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final boolean value = composite.asBoolean();

    assertTrue(value);
  }

  @Test
  void testAsBoolean_SecondBoolean() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.string("first");
    final ConfigurationNode second = ConfigurationNode.bool(false);
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final boolean value = composite.asBoolean();

    assertFalse(value);
  }

  @Test
  void testAsBoolean_NoneBoolean() {
    final ConfigurationNode nonBoolean = ConfigurationNode.string("non-boolean");
    final ConfigurationNode composite = new CompositeNode(List.of(nonBoolean, nonBoolean));

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, composite::asBoolean);

    assertIterableEquals(
        List.of(ConfigurationError.error("Must be a boolean (true/false)")), thrown.errors());
  }

  @Test
  void testIsNil_FirstNil() {
    final ConfigurationNode first = ConfigurationNode.nil();
    final ConfigurationNode second = ConfigurationNode.nil();
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final boolean isNil = composite.isNil();

    assertTrue(isNil);
  }

  @Test
  void testIsNil_SecondNil() {
    final ConfigurationNode first = ConfigurationNode.string("first");
    final ConfigurationNode second = ConfigurationNode.nil();
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final boolean isNil = composite.isNil();

    assertTrue(isNil);
  }

  @Test
  void testIsNil_NoneNil() {
    final ConfigurationNode nonNil = ConfigurationNode.string("non-nil");
    final ConfigurationNode composite = new CompositeNode(List.of(nonNil, nonNil));

    final boolean isNil = composite.isNil();

    assertFalse(isNil);
  }

  @Test
  void testAsList_FirstList() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.list(ConfigurationNode.string("first[0]"));
    final ConfigurationNode second = ConfigurationNode.list(ConfigurationNode.string("second[0]"));
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final List<ConfigurationNode> values = composite.asList();

    assertEquals(List.of(ConfigurationNode.string("first[0]")), values);
  }

  @Test
  void testAsList_SecondList() throws DeserializationException {
    final ConfigurationNode first = ConfigurationNode.string("first");
    final ConfigurationNode second = ConfigurationNode.list(ConfigurationNode.string("second[0]"));
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final List<ConfigurationNode> values = composite.asList();

    assertEquals(List.of(ConfigurationNode.string("second[0]")), values);
  }

  @Test
  void testAsList_NoneList() {
    final ConfigurationNode first = ConfigurationNode.string("first");
    final ConfigurationNode second = ConfigurationNode.string("second");
    final ConfigurationNode composite = new CompositeNode(List.of(first, second));

    final DeserializationException thrown =
        assertThrows(DeserializationException.class, composite::asList);

    assertIterableEquals(List.of(ConfigurationError.error("Must be a list")), thrown.errors());
  }
}
