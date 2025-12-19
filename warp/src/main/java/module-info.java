import me.sparky983.warp.Warp;
import org.jspecify.annotations.NullMarked;

/**
 * Warp is a powerful annotation-based configuration library.
 *
 * @see Warp
 */
@SuppressWarnings("module")
@NullMarked
module me.sparky983.warp {
  requires static org.jetbrains.annotations;
  requires static org.jspecify;

  exports me.sparky983.warp;
}
