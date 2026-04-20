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
    api("net.kyori:adventure-text-minimessage:5.0.0")

    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
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
    withType<JavaCompile> {
        options.release.set(21)
    }
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
