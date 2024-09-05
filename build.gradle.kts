object Constants {
    const val VERSION = "0.1.0"

    const val VERSION_JAVA = 21
    const val VERSION_MINECRAFT = "24w36a"
}

plugins {
    id("fabric-loom") version "1.7.3"
}

base {
    group = "io.github.startsmercury.totem_no_shading"
    archivesName = "totem-no-shading"
    version = createVersionString()
}

java {
    withSourcesJar()

    toolchain {
        languageVersion = JavaLanguageVersion.of(Constants.VERSION_JAVA)
    }
}

loom {
    accessWidenerPath = file("src/client/resources/simply-no-shading.accesswidener")
    runtimeOnlyLog4j = true
    splitEnvironmentSourceSets()

    mods.register("totem-no-shading") {
        sourceSet("main")
        sourceSet("client")
    }
}

repositories {

}

dependencies {
    minecraft("com.mojang:minecraft:${Constants.VERSION_MINECRAFT}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.3")
}

tasks.withType<ProcessResources> {
    val data = mapOf(
        "version" to Constants.VERSION,
        "version_java" to Constants.VERSION_JAVA,
        "version_minecraft" to "1.21.2-alpha.24.36.a",
    )

    inputs.properties(data)

    filesMatching("fabric.mod.json") {
        expand(data)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = Constants.VERSION_JAVA
}

fun createVersionString(): String {
    val builder = StringBuilder()

    val isReleaseBuild = project.hasProperty("build.release")
    val buildId = System.getenv("GITHUB_RUN_NUMBER")

    if (isReleaseBuild) {
        builder.append(Constants.VERSION)
    } else {
        builder.append(Constants.VERSION.substringBefore('-'))
        builder.append("-snapshot")
    }

    builder.append("+mc").append(Constants.VERSION_MINECRAFT)

    if (!isReleaseBuild) {
        if (buildId != null) {
            builder.append("-build.${buildId}")
        } else {
            builder.append("-local")
        }
    }

    return builder.toString()
}
