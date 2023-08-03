plugins {
    id("warp.library-conventions")
}

dependencies {
    api(project(":warp"))

    implementation("org.yaml:snakeyaml:2.0")
}
