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
    compileOnly("org.jetbrains:annotations:26.0.2-1")
    compileOnly("org.jspecify:jspecify:1.0.0")

    testImplementation(platform("org.junit:junit-bom:5.14.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

idea {
    module {
        sourceDirs.remove(file("src/acceptanceTest/java"))
        testSources.from("src/acceptanceTest/java")
    }
}

spotless {
    java {
        googleJavaFormat("1.17.0")
        formatAnnotations()
        toggleOffOn()
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
    withType<JavaCompile> {
        options.release.set(17)
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
    withType<Test> {
        jvmArgs("-Dfile.encoding=UTF-8")
    }
}
