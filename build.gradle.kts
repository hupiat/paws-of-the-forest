plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.10"
}

group = "org.warriorcats"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    // Paper mappings
    paperDevBundle("1.20.1-R0.1-SNAPSHOT")

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
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("org.hibernate", "org.warriorcats.libs.hibernate")
        relocate("jakarta.xml.bind", "org.warriorcats.libs.jaxb")
        relocate("org.glassfish.jaxb", "org.warriorcats.libs.jaxb.runtime")
        minimize()
    }
}
