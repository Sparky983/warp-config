plugins {
    id("warp.library-conventions")
    id("warp.test-fixtures-conventions")
}

dependencies {
    api(project(":warp"))

    implementation("com.amihaiemil.web:eo-yaml:7.0.9")
}
