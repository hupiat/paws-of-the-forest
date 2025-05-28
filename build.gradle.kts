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
    flatDir {
        dirs("libs")
    }
    mavenLocal()
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    paperDevBundle("1.20.1-R0.1-SNAPSHOT")

    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.2")
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
