package me.sparky983.warp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

@MockitoSettings
class RendererTest {
  @Test
  void testOf_Null() {
    assertThrows(NullPointerException.class, () -> Renderer.of(null));
  }

  @Test
  void testRender_Null() {
    final Renderer<String> renderer = Renderer.of("some string");

    assertThrows(NullPointerException.class, () -> renderer.render(null));
  }

  @Test
  void testRender(@Mock final Renderer.Context context) {
    final Renderer<String> renderer = Renderer.of("some string");

    assertEquals("some string", renderer.render(context));
    verifyNoInteractions(context);
  }
}
