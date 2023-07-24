rootProject.name = "warp-config"

sequenceOf(
    "warp-annotations",
    "warp"
).forEach { include(it) }
