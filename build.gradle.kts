import com.jfrog.bintray.gradle.BintrayExtension
import java.util.Date

plugins {
    kotlin("jvm") version "1.3.70"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
}

group = "com.sparetimedevs"
version = "0.0.1-EXPERIMENTAL-we9mk5l"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
    maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/") } // for SNAPSHOT builds
}

dependencies {
    val azureFunctionsGroup = "com.microsoft.azure.functions"
    val arrowGroup = "io.arrow-kt"
    val kotestGroup = "io.kotest"
    val mockkGroup = "io.mockk"

    val azureFunctionsArtifact = "azure-functions-java-library"
    val arrowFxArtifact = "arrow-fx"
    val kotestRunnerJUnit5Artifact = "kotest-runner-junit5-jvm"
    val kotestAssertionsCoreArtifact = "kotest-assertions-core-jvm"
    val kotestPropertyArtifact = "kotest-property-jvm"
    val mockkArtifact = "mockk"

    val azureFunctionsVersion: String by project
    val arrowVersion: String by project
    val kotestVersion: String by project
    val mockkVersion: String by project

    api(azureFunctionsGroup, azureFunctionsArtifact, azureFunctionsVersion)
    api(arrowGroup, arrowFxArtifact, arrowVersion)

    implementation(kotlin("stdlib-jdk8"))

    testImplementation(kotestGroup, kotestRunnerJUnit5Artifact, kotestVersion)
    testImplementation(kotestGroup, kotestAssertionsCoreArtifact, kotestVersion)
    testImplementation(kotestGroup, kotestPropertyArtifact, kotestVersion)
    testImplementation(mockkGroup, mockkArtifact, mockkVersion)
}

sourceSets {
    getByName("test").java.srcDirs("src/test/kotlin")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
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
        repo = "Pofpaf"
        name = "Pofpaf"
        userOrg = "sparetimedevs"
        setLabels("kotlin")
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/sparetimedevs/pofpaf.git"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as? String
            released = Date().toString()
        })
    })
}
