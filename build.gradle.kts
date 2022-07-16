plugins {
    id("fabric-loom") version "0.11-SNAPSHOT"
}

group = "org.syringemc"
version = "0.1.0+1.19"

tasks.processResources {
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
    minecraft("com.mojang:minecraft:1.19")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.19+build.4", classifier = "v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.8")
    arrayOf(
        "fabric-api-base",
        "fabric-command-api-v2",
        "fabric-lifecycle-events-v1",
        "fabric-registry-sync-v0",
        "fabric-resource-loader-v0",
        "fabric-key-binding-api-v1",
        "fabric-networking-api-v1",
    ).forEach { modImplementation(fabricApi.module(it, "0.57.0+1.19")) }
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
