rootProject.name = "warp-config"

sequenceOf(
    "warp-annotations",
    "warp-api"
).forEach { include(it) }
