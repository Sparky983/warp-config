rootProject.name = "warp-config"

sequenceOf("warp", "warp-adventure", "warp-yaml").forEach { include(it) }
