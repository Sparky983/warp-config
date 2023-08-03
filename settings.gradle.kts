rootProject.name = "warp-config"

sequenceOf("warp", "warp-snakeyaml").forEach { include(it) }
