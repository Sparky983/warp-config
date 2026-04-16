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
    api("com.fasterxml.jackson.core:jackson-core:2.16.0")
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = SourcesJar.Sources(),
    ))
    signAllPublications()
    publishToMavenCentral()
}
