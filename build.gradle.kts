plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.7"
}

group = "org.warriorcats"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper mappings
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.1-R0.1-SNAPSHOT")

    // Compile-only dependencies
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("org.projectlombok:lombok:1.18.30")

    // Annotation processor for Lombok
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // ORM & SQL
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.1")

    // JAXB
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.2")

    // Logging
    implementation("org.jboss.logging:jboss-logging:3.5.3.Final")

    // MySQL
    implementation("com.mysql:mysql-connector-j:8.4.0")
}

tasks {
    jar {
        archiveClassifier.set("fat")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(sourceSets.main.get().output)

        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith(".jar") }
                .map { zipTree(it) }
        })

        from("src/main/resources") {
            include("plugin.yml")
        }
    }

    build {
        dependsOn(jar)
    }
}

val runServer by tasks.registering(JavaExec::class) {
    group = "paper"
    description = "Start local Paper server."

    val paperJar = rootProject.layout.buildDirectory.file("libs/${rootProject.name}-${version}-dev.jar")

    val serverDir = file("run/")

    val pluginsDir = serverDir.resolve("plugins")
    pluginsDir.mkdirs()

    doFirst {
        val pluginJar = paperJar.get().asFile
        val target = pluginsDir.resolve(pluginJar.name)
        pluginJar.copyTo(target, overwrite = true)
    }

    workingDir = serverDir
    mainClass.set("-jar")
    args = listOf(
        "paper-1.21.1-133.jar",
        "--nogui"
    )
    standardInput = System.`in`
}

val debugServer by tasks.registering(JavaExec::class) {
    group = "paper"
    description = "Start local Paper server in debug mode."

    val paperJar = file("run/paper-1.21.1-133.jar")
    val serverDir = file("run/")
    val pluginsDir = serverDir.resolve("plugins")
    pluginsDir.mkdirs()

    doFirst {
        val pluginJar = layout.buildDirectory.file("libs/${rootProject.name}-${version}-dev.jar").get().asFile
        val target = pluginsDir.resolve(pluginJar.name)
        pluginJar.copyTo(target, overwrite = true)
    }

    workingDir = serverDir
    standardInput = System.`in`
    mainClass.set("-jar")
    args = listOf(paperJar.name, "--nogui")

    // Add remote debug JVM arguments
    jvmArgs = listOf(
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
        "-Xmx2G",
        "-Xms1G"
    )
}
