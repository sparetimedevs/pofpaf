plugins {
    kotlin("jvm") version "1.3.70"
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
