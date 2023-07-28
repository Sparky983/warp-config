plugins {
    id("warp.library-conventions")
    `java-test-fixtures`
}

dependencies {
    api(project(":warp-annotations"))
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
