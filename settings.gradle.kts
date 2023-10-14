rootProject.name = "warp-config"

sequenceOf("warp", "warp-yaml").forEach { include(it) }
