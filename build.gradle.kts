plugins {
    id("fabric-loom") version "0.11-SNAPSHOT"
}

group = "org.syringemc"
version = "0.1.0+1.18"

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

sourceSets {
    val main = getByName("main")
    create("testmod") {
        compileClasspath += main.compileClasspath
        compileClasspath += main.output
        runtimeClasspath += main.runtimeClasspath
    }
}

repositories {
    mavenCentral()
}

fun DependencyHandlerScope.includeAndModImplementation(dep: Any) {
    modImplementation(dep)
    include(dep)
}

dependencies {
    minecraft("com.mojang:minecraft:1.18.2")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.18.2+build.2", classifier = "v2")
    modImplementation("net.fabricmc:fabric-loader:0.13.3")
    arrayOf(
        "fabric-api-base",
        "fabric-lifecycle-events-v1",
        "fabric-resource-loader-v0",
        "fabric-key-binding-api-v1",
        "fabric-networking-api-v1",
    ).forEach { includeAndModImplementation(fabricApi.module(it, "0.48.0+1.18.2")) }
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.48.0+1.18.2"))
}

loom {
    accessWidenerPath.set(file("src/main/resources/syringe.accesswidener"))
    runs {
        create("testmodClient") {
            client()
            ideConfigGenerated(project.rootProject == project)
            name("Testmod Client")
            source(sourceSets.getByName("testmod"))
        }
    }
}
