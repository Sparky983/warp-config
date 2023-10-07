plugins {
    id("warp.library-conventions")
    `java-test-fixtures`
}

dependencies {
    testImplementation("org.mockito:mockito-core:5.6.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.6.0")
}

idea {
    module {
        sourceDirs.remove(file("src/testFixtures/java"))
        testSources.from("src/testFixtures/java")
    }
}
