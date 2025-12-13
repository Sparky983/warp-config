package me.sparky983.warp.internal.deserializers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.sparky983.warp.ConfigurationError;
import me.sparky983.warp.ConfigurationNode;
import me.sparky983.warp.Configurations;
import me.sparky983.warp.DeserializationException;
import me.sparky983.warp.Deserializer;
import me.sparky983.warp.Renderer;
import me.sparky983.warp.internal.DeserializerFactory;
import me.sparky983.warp.internal.DeserializerRegistry;
import me.sparky983.warp.internal.ParameterizedType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class ConfigurationDeserializerFactoryTest {
  @Mock DeserializerRegistry deserializers;
  @Mock Deserializer.Context deserializerContext;
  @Mock Renderer.Context rendererContext;
  @Mock Deserializer<String> propertyDeserializer;
  DeserializerFactory factory;

  @BeforeEach
  void setUp() {
    factory = new ConfigurationDeserializerFactory();
  }

  @AfterEach
  void tearDown() {
    verifyNoMoreInteractions(
        deserializers, deserializerContext, rendererContext, propertyDeserializer);
  }

  @Test
  void testGet_NullType() {
    assertThrows(NullPointerException.class, () -> factory.create(deserializers, null));
  }

  @Test
  void testGet_NotAnnotated() {
    assertEquals(
        Optional.empty(),
        factory.create(
            deserializers, ParameterizedType.of(Configurations.MissingAnnotation.class)));
  }

  @Test
  void testGet_Invalid() {
    assertThrows(
        IllegalStateException.class,
        () -> factory.create(deserializers, ParameterizedType.of(Configurations.Sealed.class)));
  }

  @Test
  void testDeserialize_NullNodeRequiredProperty() throws DeserializationException {
    final ConfigurationError error = ConfigurationError.error("Some error");

    when(propertyDeserializer.deserialize(null, deserializerContext))
        .thenThrow(new DeserializationException(error));
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(propertyDeserializer));

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(null, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.group("property", error)), thrown.errors());
  }

  @Test
  void testDeserialize_NullNodeOptionalProperties() throws DeserializationException {
    when(propertyDeserializer.deserialize(null, deserializerContext))
        .thenReturn(Renderer.of("some string"));
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(propertyDeserializer));

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(null, deserializerContext));

    assertIterableEquals(
        List.of(ConfigurationError.error("Must be set to a value")), thrown.errors());
  }

  @Test
  void testDeserialize_NullContext() {
    final Deserializer<? extends Configurations.Empty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.nil();

    assertThrows(NullPointerException.class, () -> deserializer.deserialize(node, null));
  }

  @Test
  void testDeserialize_NonMap() {
    final Deserializer<? extends Configurations.Empty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.nil();

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.error("Must be a map")), thrown.errors());
  }

  @Test
  void testDeserialize_PropertyHasErrorDuringDeserialization() throws DeserializationException {
    final ConfigurationNode property = ConfigurationNode.string("some string");
    final ConfigurationError error = ConfigurationError.error("Some error");

    when(propertyDeserializer.deserialize(property, deserializerContext))
        .thenThrow(new DeserializationException(error));
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(propertyDeserializer));

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node = ConfigurationNode.map(Map.entry("property", property));

    final DeserializationException thrown =
        assertThrows(
            DeserializationException.class,
            () -> deserializer.deserialize(node, deserializerContext));

    assertIterableEquals(List.of(ConfigurationError.group("property", error)), thrown.errors());

    verify(propertyDeserializer).deserialize(any(), any());
    verify(deserializers).get(ParameterizedType.of(String.class));
  }

  @Test
  void testDeserialize_PropertyHasNoDeserializer() {
    when(deserializers.get(ParameterizedType.of(String.class))).thenReturn(Optional.empty());

    assertThrows(
        IllegalStateException.class,
        () -> factory.create(deserializers, ParameterizedType.of(Configurations.String.class)));

    verify(deserializers).get(ParameterizedType.of(String.class));
  }

  @Test
  void testRender_NullContext() throws DeserializationException {
    final Deserializer<? extends Configurations.Empty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.Empty.class))
            .orElseThrow(AssertionError::new);

    final Renderer<? extends Configurations.Empty> renderer =
        deserializer.deserialize(ConfigurationNode.map(), deserializerContext);

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender_OuterArgumentsOnly(@Mock final Renderer<String> propertyRenderer)
      throws DeserializationException, NoSuchMethodException {
    interface DummyParameter {
      void dummyParameter(Object dummyParameter);
    }

    final Parameter outerParameter =
        DummyParameter.class.getDeclaredMethod("dummyParameter", Object.class).getParameters()[0];
    final Object outerArgument = new Object();

    when(deserializerContext.parameters()).thenReturn(new Parameter[] {outerParameter});
    when(rendererContext.arguments()).thenReturn(new Object[] {outerArgument});
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(propertyDeserializer));
    when(propertyDeserializer.deserialize(any(), any())).thenReturn(propertyRenderer);
    when(propertyRenderer.render(any())).thenReturn("value");

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node =
        ConfigurationNode.map(Map.of("property", ConfigurationNode.string("value")));

    final Renderer<? extends Configurations.String> renderer =
        deserializer.deserialize(node, deserializerContext);

    final Configurations.String configuration = renderer.render(rendererContext);

    assertEquals("value", configuration.property());
    verify(propertyDeserializer)
        .deserialize(
            eq(ConfigurationNode.string("value")),
            argThat(
                (context) ->
                    context.parameters().length == 1
                        && context.parameters()[0].equals(outerParameter)));
    verify(propertyRenderer)
        .render(
            argThat(
                (context) ->
                    context.arguments().length == 1 && context.arguments()[0] == outerArgument));
    verifyNoMoreInteractions(propertyRenderer);
  }

  @Test
  void testRender_InnerArgumentsOnly(@Mock final Renderer<String> propertyRenderer)
      throws DeserializationException, NoSuchMethodException {
    final Parameter innerParameter =
        Configurations.ParameterizedProperty.class.getDeclaredMethod("property", Object.class)
            .getParameters()[0];
    final Object innerArgument = new Object();

    when(deserializerContext.parameters()).thenReturn(new Parameter[0]);
    when(rendererContext.arguments()).thenReturn(new Object[0]);
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(propertyDeserializer));
    when(propertyDeserializer.deserialize(any(), any())).thenReturn(propertyRenderer);
    when(propertyRenderer.render(any())).thenReturn("value");

    final Deserializer<? extends Configurations.ParameterizedProperty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.ParameterizedProperty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node =
        ConfigurationNode.map(Map.of("property", ConfigurationNode.string("value")));

    final Renderer<? extends Configurations.ParameterizedProperty> renderer =
        deserializer.deserialize(node, deserializerContext);

    final Configurations.ParameterizedProperty configuration = renderer.render(rendererContext);

    assertEquals("value", configuration.property(innerArgument));
    verify(propertyDeserializer)
        .deserialize(
            eq(ConfigurationNode.string("value")),
            argThat(
                (context) ->
                    context.parameters().length == 1
                        && context.parameters()[0].equals(innerParameter)));
    verify(propertyRenderer)
        .render(
            argThat(
                (context) ->
                    context.arguments().length == 1 && context.arguments()[0] == innerArgument));
    verifyNoMoreInteractions(propertyRenderer);
  }

  @Test
  void testRender_CombinedArguments(@Mock final Renderer<String> propertyRenderer)
      throws DeserializationException, NoSuchMethodException {
    interface DummyParameter {
      void dummyParameter(Object dummyParameter);
    }

    final Parameter outerParameter =
        DummyParameter.class.getDeclaredMethod("dummyParameter", Object.class).getParameters()[0];
    final Parameter innerParameter =
        Configurations.ParameterizedProperty.class.getDeclaredMethod("property", Object.class)
            .getParameters()[0];
    final Object outerArgument = new Object();
    final Object innerArgument = new Object();

    when(deserializerContext.parameters()).thenReturn(new Parameter[] {outerParameter});
    when(rendererContext.arguments()).thenReturn(new Object[] {outerArgument});
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(propertyDeserializer));
    when(propertyDeserializer.deserialize(any(), any())).thenReturn(propertyRenderer);
    when(propertyRenderer.render(any())).thenReturn("value");

    final Deserializer<? extends Configurations.ParameterizedProperty> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.ParameterizedProperty.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node =
        ConfigurationNode.map(Map.of("property", ConfigurationNode.string("value")));

    final Renderer<? extends Configurations.ParameterizedProperty> renderer =
        deserializer.deserialize(node, deserializerContext);

    final Configurations.ParameterizedProperty configuration = renderer.render(rendererContext);

    assertEquals("value", configuration.property(innerArgument));
    verify(propertyDeserializer)
        .deserialize(
            eq(ConfigurationNode.string("value")),
            argThat(
                (context) ->
                    context.parameters().length == 2
                        && context.parameters()[0].equals(outerParameter)
                        && context.parameters()[1].equals(innerParameter)));
    verify(propertyRenderer)
        .render(
            argThat(
                (context) ->
                    context.arguments().length == 2
                        && context.arguments()[0] == outerArgument
                        && context.arguments()[1] == innerArgument));
    verifyNoMoreInteractions(propertyRenderer);
  }

  @Test
  void testRender(@Mock final Renderer<String> propertyRenderer) throws DeserializationException {
    when(deserializers.get(ParameterizedType.of(String.class)))
        .thenReturn(Optional.of(propertyDeserializer));
    when(propertyDeserializer.deserialize(eq(ConfigurationNode.string("value")), any()))
        .thenReturn(propertyRenderer);
    when(propertyRenderer.render(any())).thenReturn("value");
    when(deserializerContext.parameters()).thenReturn(new Parameter[0]);
    when(rendererContext.arguments()).thenReturn(new Object[0]);

    final Deserializer<? extends Configurations.String> deserializer =
        factory
            .create(deserializers, ParameterizedType.of(Configurations.String.class))
            .orElseThrow(AssertionError::new);

    final ConfigurationNode node =
        ConfigurationNode.map(Map.of("property", ConfigurationNode.string("value")));

    final Renderer<? extends Configurations.String> renderer =
        deserializer.deserialize(node, deserializerContext);

    final Configurations.String string = renderer.render(rendererContext);

    assertEquals("value", string.property());
    verify(propertyDeserializer)
        .deserialize(
            eq(ConfigurationNode.string("value")),
            argThat((context) -> context.parameters().length == 0));
    verify(propertyRenderer).render(argThat((context) -> context.arguments().length == 0));
    verifyNoMoreInteractions(propertyRenderer);
  }
}
