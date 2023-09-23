import org.jspecify.annotations.NullMarked;

@NullMarked
module me.sparky983.warp.yaml {
  requires transitive me.sparky983.warp;
  requires com.amihaiemil.eoyaml;

  requires static org.jspecify;
  exports me.sparky983.warp.yaml;
}
