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
