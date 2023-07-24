import org.jspecify.annotations.NullMarked;

@NullMarked
module me.sparky983.warp.api {
  requires static org.jetbrains.annotations;
  requires static org.jspecify;
  requires transitive me.sparky983.warp.annotations;

  exports me.sparky983.warp;
}
