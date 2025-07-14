import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("warp.library-conventions")
    id("warp.test-fixtures-conventions")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

repositories {
    maven("https://repo.sparky983.me/snapshots")
}

dependencies {
    api(project(":warp"))

    implementation("com.amihaiemil.web:eo-yaml:7.2.0")
}

mavenPublishing {
    configure(JavaLibrary(
        javadocJar = JavadocJar.Javadoc(),
        sourcesJar = true,
    ))
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
}
