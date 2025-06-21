import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("warp.library-conventions")
    id("com.vanniktech.maven.publish") version "0.32.0"
    `java-test-fixtures`
}

dependencies {
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true,
    ))
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}

idea {
    module {
        sourceDirs.remove(file("src/testFixtures/java"))
        testSources.from("src/testFixtures/java")
    }
}
