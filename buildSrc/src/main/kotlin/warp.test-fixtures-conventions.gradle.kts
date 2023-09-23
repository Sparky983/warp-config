plugins {
    idea
    `java-test-fixtures`
}

idea {
    module {
        sourceDirs.remove(file("src/testFixtures/java"))
        testSources.from("src/testFixtures/java")
    }
}
