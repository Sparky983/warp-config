plugins {
    id("warp.library-conventions")
    id("warp.test-fixtures-conventions")
}

repositories {
    maven("https://repo.sparky983.me/snapshots")
}

dependencies {
    api(project(":warp"))

    implementation("me.sparky983:eo-yaml:7.0.10-SNAPSHOT")}
