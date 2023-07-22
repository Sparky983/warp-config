import org.jspecify.annotations.NullMarked;

@NullMarked
module me.sparky983.warp.annotations {
  requires static org.jetbrains.annotations;
  requires static org.jspecify;

  exports me.sparky983.warp.annotations;
}
