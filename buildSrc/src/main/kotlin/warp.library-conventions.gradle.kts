plugins {
    `java-library`
    id("com.github.sherter.google-java-format")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.jspecify:jspecify:0.3.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc {
        options {
            (this as StandardJavadocDocletOptions).run {
                tags("warp.implNote:a:Implementation Note:")
                tags("warp.apiNote:a:API Note:")
            }
        }
    }
    test {
        useJUnitPlatform()
    }
}
