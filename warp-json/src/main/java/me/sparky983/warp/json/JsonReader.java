package me.sparky983.warp.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.sparky983.warp.ConfigurationNode;

final class JsonReader {
  static ConfigurationNode read(final JsonParser parser) throws IOException {
    parser.nextToken();
    final ConfigurationNode node = readJson(parser);
    if (parser.currentToken() != null) {
      throw new JsonParseException("Expected end of file");
    }
    return node;
  }

  private static ConfigurationNode readJson(final JsonParser parser) throws IOException {
    return switch (parser.currentToken()) {
      case START_OBJECT -> readObject(parser);
      case START_ARRAY -> readArray(parser);
      case VALUE_STRING -> {
        final String value = parser.getValueAsString();
        parser.nextToken();
        yield ConfigurationNode.string(value);
      }
      case VALUE_NUMBER_INT -> {
        final long value = parser.getLongValue();
        parser.nextToken();
        yield ConfigurationNode.integer(value);
      }
      case VALUE_NUMBER_FLOAT -> {
        final double value = parser.getDoubleValue();
        parser.nextToken();
        yield ConfigurationNode.decimal(value);
      }
      case VALUE_TRUE -> {
        parser.nextToken();
        yield ConfigurationNode.bool(true);
      }
      case VALUE_FALSE -> {
        parser.nextToken();
        yield ConfigurationNode.bool(false);
      }
      case VALUE_NULL -> {
        parser.nextToken();
        yield ConfigurationNode.nil();
      }
      default -> throw new JsonParseException("Unexpected token " + parser.getText());
    };
  }

  private static ConfigurationNode readArray(final JsonParser parser) throws IOException {
    assert parser.currentToken() == JsonToken.START_ARRAY;

    parser.nextToken();

    final List<ConfigurationNode> elements = new ArrayList<>();

    while (parser.currentToken() != JsonToken.END_ARRAY) {
      elements.add(readJson(parser));
    }

    parser.nextToken();

    return ConfigurationNode.list(elements);
  }

  private static ConfigurationNode readObject(final JsonParser parser) throws IOException {
    assert parser.currentToken() == JsonToken.START_OBJECT;

    parser.nextToken();

    final Map<String, ConfigurationNode> members = new LinkedHashMap<>();

    while (parser.currentToken() != JsonToken.END_OBJECT) {
      // JSON allows for duplicate keys, but just let them override each other
      final String key = parser.currentName();

      if (key == null) {
        throw new JsonParseException("Expected a key");
      }

      parser.nextToken();

      members.put(key, readJson(parser));
    }

    parser.nextToken();

    return ConfigurationNode.map(members);
  }
}
