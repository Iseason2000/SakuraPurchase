plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.8.20"
}

group = "top.iseason.bukkittemplate"

val exposedVersion: String by rootProject
repositories {
    maven {
        name = "MMOItems"
        url = uri("https://nexus.phoenixdevt.fr/repository/maven-public/")
    }
}
dependencies {
//    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.8.10")

    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("net.kyori:adventure-text-minimessage:4.13.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("org.bstats:bstats-bukkit:3.0.1")

    compileOnly("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT") { isTransitive = false }
    compileOnly("com.github.LoneDev6:api-itemsadder:3.4.1-r4") { isTransitive = false }
    compileOnly("com.github.oraxen:oraxen:1.155.3") { isTransitive = false }
}
tasks {
    compileJava {
        options.isFailOnError = false
        options.isWarnings = false
        options.isVerbose = false
    }
    build {
        dependsOn(named("shadowJar"))
    }
    dokkaHtml.configure {
        dokkaSourceSets {
            named("main") {
                moduleName.set("BukkitTemplate")
            }
        }
    }
}