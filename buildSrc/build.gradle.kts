plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.20.0")

    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.jspecify:jspecify:0.3.0")

    implementation(platform("org.junit:junit-bom:5.9.3"))
    implementation("org.junit.jupiter:junit-jupiter-api")
    implementation("org.junit.jupiter:junit-jupiter-engine")
}
