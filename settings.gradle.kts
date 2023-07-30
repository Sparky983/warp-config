rootProject.name = "warp-config"

sequenceOf(
        "warp",
        "warp-annotations"
).forEach { include(it) }
