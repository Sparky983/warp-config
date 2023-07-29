plugins {
    id("warp.library-conventions")
    `java-test-fixtures`
}

dependencies {
    api(project(":warp-annotations"))

    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
}

idea {
    module {
        sourceDirs.remove(file("src/testFixtures/java"))
        testSources.from("src/testFixtures/java")
    }
}

tasks {
    javadoc {
        (options as CoreJavadocOptions).run {
            addStringOption("source", "20")
            addBooleanOption("-enable-preview", true)
        }
    }
    withType<JavaCompile> {
        options.compilerArgs.add("--enable-preview")
    }
    withType<Test> {
        jvmArgs("--enable-preview")
    }
}
