import com.jfrog.bintray.gradle.BintrayExtension
import java.util.Date

plugins {
    kotlin("jvm") version "1.4.10"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
    id("com.jfrog.artifactory") version "4.15.2"
}

group = "com.sparetimedevs"
version = "0.0.2-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
}

dependencies {
    val arrowGroup = "io.arrow-kt"
    val kotestGroup = "io.kotest"
    val mockkGroup = "io.mockk"
    val azureFunctionsGroup = "com.microsoft.azure.functions"
    
    val arrowCoreDataArtifact = "arrow-core-data"
    val arrowFxCoroutinesArtifact = "arrow-fx-coroutines"
    val kotestRunnerJUnit5Artifact = "kotest-runner-junit5-jvm"
    val kotestAssertionsCoreArtifact = "kotest-assertions-core-jvm"
    val kotestAssertionsArrowArtifact = "kotest-assertions-arrow-jvm"
    val kotestPropertyArtifact = "kotest-property-jvm"
    val mockkArtifact = "mockk"
    val azureFunctionsArtifact = "azure-functions-java-library"
    
    val arrowVersion: String by project
    val kotestVersion: String by project
    val mockkVersion: String by project
    val azureFunctionsVersion: String by project
    
    api(arrowGroup, arrowCoreDataArtifact, arrowVersion)
    
    implementation(arrowGroup, arrowFxCoroutinesArtifact, arrowVersion)
    
    testImplementation(kotestGroup, kotestRunnerJUnit5Artifact, kotestVersion)
    testImplementation(kotestGroup, kotestAssertionsCoreArtifact, kotestVersion)
    testImplementation(kotestGroup, kotestAssertionsArrowArtifact, kotestVersion) { exclude(arrowGroup) }
    testImplementation(kotestGroup, kotestPropertyArtifact, kotestVersion)
    testImplementation(mockkGroup, mockkArtifact, mockkVersion)
    testImplementation(azureFunctionsGroup, azureFunctionsArtifact, azureFunctionsVersion)
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

val bintrayUsername: String? by project
val bintrayApiKey: String? by project

bintray {
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

artifactory {
    setContextUrl("https://oss.jfrog.org")
    publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
        repository(delegateClosureOf<groovy.lang.GroovyObject> {
            setProperty("repoKey", "oss-snapshot-local")
            setProperty("username", bintrayUsername)
            setProperty("password", bintrayApiKey)
        })
        defaults(delegateClosureOf<groovy.lang.GroovyObject> {
            invokeMethod("publications", publishing.publications.names.toTypedArray())
            setProperty("publishArtifacts", true)
            setProperty("publishPom", true)
        })
    })
    resolve(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig> {
        setProperty("repoKey", "jcenter")
    })
}
