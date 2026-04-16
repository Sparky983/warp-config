import org.jspecify.annotations.NullMarked;

@NullMarked
module me.sparky983.warp.json {
  requires transitive me.sparky983.warp;
  requires com.fasterxml.jackson.core;
  requires static org.jspecify;

  exports me.sparky983.warp.json;
}
