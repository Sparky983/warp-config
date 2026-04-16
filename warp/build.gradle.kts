import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    id("warp.library-conventions")
    id("com.vanniktech.maven.publish") version "0.36.0"
    `java-test-fixtures`
}

dependencies {
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

idea {
    module {
        sourceDirs.remove(file("src/testFixtures/java"))
        testSources.from("src/testFixtures/java")
    }
}
