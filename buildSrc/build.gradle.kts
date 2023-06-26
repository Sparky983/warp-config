plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.com.github.sherter.google-java-format:google-java-format-gradle-plugin:0.9")

    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.jspecify:jspecify:0.3.0")

    implementation(platform("org.junit:junit-bom:5.9.1"))
    implementation("org.junit.jupiter:junit-jupiter-api")
    implementation("org.junit.jupiter:junit-jupiter-engine")
}
