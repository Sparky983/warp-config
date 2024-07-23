plugins {
    `java-library`
    `maven-publish`

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
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.jspecify:jspecify:1.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "sparky983"
            url = uri(
                if (version.toString().endsWith("-SNAPSHOT")) {
                    "https://repo.sparky983.me/snapshots"
                } else {
                    "https://repo.sparky983.me/releases"
                },
            )
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
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
