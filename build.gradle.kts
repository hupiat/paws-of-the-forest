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
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper mappings
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.21.1-R0.1-SNAPSHOT")

    // Compile-only dependencies
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly(files("run/plugins/ModelEngine-4.0.8.jar"))
    compileOnly(files("run/plugins/LibsDisguises-11.0.6-Free.jar"))
    compileOnly(files("run/plugins/MythicMobs-5.8.2.jar"))

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

    dependsOn(tasks.named("build"))

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
