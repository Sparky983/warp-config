plugins {
    id("warp.library-conventions")
    id("warp.test-fixtures-conventions")
}

dependencies {
    api(project(":warp"))
    api("net.kyori:adventure-text-minimessage:4.25.0")

    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).run {
        links("https://jd.advntr.dev/api/4.25.0/")
        links("https://jd.advntr.dev/text-minimessage/4.25.0/")
    }
}
