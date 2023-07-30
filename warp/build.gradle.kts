plugins {
    id("warp.library-conventions")
    `java-test-fixtures`
}

dependencies {
    api(project(":warp-annotations"))
}

idea {
    module {
        sourceDirs.remove(file("src/testFixtures/java"))
        testSources.from("src/testFixtures/java")
    }
}

spotless {
    java {
        // use preview features
        targetExclude(
                "src/main/java/me/sparky983/warp/ConfigurationError.java",
                "src/main/java/me/sparky983/warp/ConfigurationException.java"
        )
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
