plugins {
    id("warp.library-conventions")
    `java-test-fixtures`
}

dependencies {
    api(project(":warp-annotations"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(20))
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
    test {
        jvmArgs("--enable-preview")
    }
}
