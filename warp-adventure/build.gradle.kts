plugins {
    id("warp.library-conventions")
}

dependencies {
    api(project(":warp"))
    api("net.kyori:adventure-text-minimessage:4.25.0")
}