import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import java.net.URI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    kotlin("jvm") version "1.7.10"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven { url = URI("https://repo.papermc.io/repository/maven-public/") }
}

group = "jp.faketuna"
version = "1.0-SNAPSHOT"
java.sourceCompatibility=JavaVersion.VERSION_17
val mcVersion = "1.19"


dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compileOnly ("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")

    // Spigot API or Use paper if you want
    compileOnly("io.papermc.paper:paper-api:${mcVersion}-R0.1-SNAPSHOT")

    library(kotlin("stdlib")) // All platforms
    library("com.google.code.gson", "gson", "2.8.7") // All platforms
    bukkitLibrary("com.google.code.gson", "gson", "2.8.7") // Bukkit only
    bukkitLibrary("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.13.3")
}


bukkit {
    main = "jp.faketuna.paper.deeplchattranslator.DeeplChatTranslator"

    apiVersion = "1.19"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    author = "ft"
    prefix = "CDT"


    commands {
        register("deeplchattranslator") {
            description = "main command of Deepl chat translator"
            aliases = listOf("dct")
            permission = "dct.command.deeplchattranslator"
            usage = "/dct"
            permissionMessage = "No permission"
        }
    }


    permissions {
        register("dct.*") {
            children = listOf("dct.command.deeplchattranslator")
        }

        register("dct.command.deeplchattranslator") {
            description = "Permission node for Deepl chat translator main command"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }

}




application {
    // Define the main class for the application.
    mainClass.set("kotlinTest")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
