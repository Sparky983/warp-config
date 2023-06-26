package me.sparky983.warp;

import java.util.Iterator;
import org.jspecify.annotations.NullMarked;

/** The default implementation of {@link ConfigurationValue.List}. */
@NullMarked
record DefaultListValue(@Override java.util.List<ConfigurationValue> values)
    implements ConfigurationValue.List {
  /**
   * Constructs the list of values.
   *
   * @param values the list of values; changes in this list will not be reflected in the created
   *     values
   * @throws NullPointerException if the values list is {@code null} or one of the values are {@code
   *     null}.
   */
  DefaultListValue(final java.util.List<ConfigurationValue> values) {
    this.values = java.util.List.copyOf(values);
  }

  @Override
  public Iterator<ConfigurationValue> iterator() {
    return values.iterator();
  }
}
