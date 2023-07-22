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
    withType<JavaCompile>() {
        options.compilerArgs.add("--enable-preview")
    }
    test {
        jvmArgs("--enable-preview")
    }
}
