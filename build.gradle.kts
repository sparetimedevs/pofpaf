import com.jfrog.bintray.gradle.BintrayExtension
import java.util.Date

plugins {
    kotlin("jvm") version "1.3.70"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

group = "com.sparetimedevs"
version = "0.0.1-EXPERIMENTAL-k23p9gp"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
    maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/") } // for SNAPSHOT builds
}

dependencies {
    val azureFunctionsGroup = "com.microsoft.azure.functions"
    val arrowGroup = "io.arrow-kt"
    val kotlinTestGroup = "io.kotlintest"
    val mockkGroup = "io.mockk"

    val azureFunctionsArtifact = "azure-functions-java-library"
    val arrowFxArtifact = "arrow-fx"
    val kotlinTestRunnerJUnit5Artifact = "kotlintest-runner-junit5"
    val mockkArtifact = "mockk"

    val azureFunctionsVersion: String by project
    val arrowVersion: String by project
    val kotlinTestVersion: String by project
    val mockkVersion: String by project

    api(azureFunctionsGroup, azureFunctionsArtifact, azureFunctionsVersion)
    api(arrowGroup, arrowFxArtifact, arrowVersion)

    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotlinTestGroup, kotlinTestRunnerJUnit5Artifact, kotlinTestVersion)
    testImplementation(mockkGroup, mockkArtifact, mockkVersion)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

artifacts {
    archives(sourcesJar)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}

bintray {
    val bintrayUsername: String? by project
    val bintrayApiKey: String? by project
    user = bintrayUsername
    key = bintrayApiKey
    setPublications("default")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "Bow"
        name = "Bow"
        userOrg = "sparetimedevs"
        setLabels("kotlin")
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/sparetimedevs/bow.git"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as? String
            released = Date().toString()
        })
    })
}
