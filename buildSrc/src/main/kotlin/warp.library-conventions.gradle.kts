plugins {
    `java-library`
    idea
    id("com.diffplug.spotless")
}

val acceptanceTest: SourceSet by sourceSets.creating

configurations[acceptanceTest.implementationConfigurationName].extendsFrom(configurations.testImplementation.get())
configurations[acceptanceTest.runtimeOnlyConfigurationName].extendsFrom(configurations.testRuntimeOnly.get())

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

idea {
    module {
        sourceDirs.remove(file("src/acceptanceTest/java"))
        testSources.from("src/acceptanceTest/java")
    }
}

spotless {
    java {
        // googleJavaFormat("1.17.0")
        formatAnnotations()
    }
}

tasks {
    val acceptanceTestTask = tasks.register<Test>("acceptanceTest") {
        description = "Runs acceptance tests."
        group = "verification"
        useJUnitPlatform()

        testClassesDirs = acceptanceTest.output.classesDirs
        classpath = configurations[acceptanceTest.runtimeClasspathConfigurationName] + acceptanceTest.output

        shouldRunAfter(tasks.test)
    }
    javadoc {
        options {
            (this as StandardJavadocDocletOptions).run {
                tags("warp.implNote:a:Implementation Note:")
                tags("warp.apiNote:a:API Note:")
            }
        }
    }
    check {
        dependsOn(acceptanceTestTask)
    }
    test {
        useJUnitPlatform()
    }
}
