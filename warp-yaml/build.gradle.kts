plugins {
    id("warp.library-conventions")
    id("warp.test-fixtures-conventions")
}

repositories {
    maven("https://repo.sparky983.me/snapshots")
}

dependencies {
    api(project(":warp"))

    implementation("com.amihaiemil.web:eo-yaml:8.0.6")
}
