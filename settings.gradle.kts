rootProject.name = "warp-config"

sequenceOf("warp-annotations")
    .forEach { include(it) }
