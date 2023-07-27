plugins {
    `java-library`
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.jspecify:jspecify:0.3.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
    withJavadocJar()
    withSourcesJar()
}

spotless {
    java {
        // googleJavaFormat("1.17.0")
        formatAnnotations()
    }
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
