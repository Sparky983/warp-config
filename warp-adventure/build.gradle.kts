import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    id("warp.library-conventions")
    id("warp.test-fixtures-conventions")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

dependencies {
    api(project(":warp"))
    api("net.kyori:adventure-text-minimessage:4.26.1")

    testImplementation("org.mockito:mockito-core:5.21.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.Javadoc(),
            sourcesJar = SourcesJar.Sources(),
    ))
    signAllPublications()
    publishToMavenCentral()
}

tasks {
    jar {
        manifest {
            attributes("Automatic-Module-Name" to "me.sparky983.warp.adventure")
        }
    }
    javadoc {
        (options as StandardJavadocDocletOptions).run {
            links("https://jd.advntr.dev/api/4.25.0/")
            links("https://jd.advntr.dev/text-minimessage/4.25.0/")
        }
    }
}
