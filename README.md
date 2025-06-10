
# 🐾 Paws of the Forest

**Paws of the Forest** is a Minecraft plugin for **Paper 1.21.1**, inspired by the world of feline clans.  
It enriches gameplay with advanced systems such as role management, dynamic weather, economy, moderation tools, and more.

---

## ✅ Requirements

- Java 17 or higher
- [Maven](https://maven.apache.org/)
- A Minecraft **Paper 1.21.1** server

---

## 🛠️ Build Instructions

To compile the plugin, run:

```bash
./gradlew clean build
```

The compiled `.jar` will be located at:

```
build/libs/paws-of-the-forest-1.0.0.jar
```

---

## 📦 Installation

Copy the `.jar` into your Paper server's `plugins/` folder:

```bash
cp build/libs/paws-of-the-forest-1.0.0-dev.jar ../Minecraft_server_paper_1.21.1/plugins/
```

> ⚠️ **Important:** The path `../Minecraft_server_paper_1.21.1/` is just an example.  
> **You must update this path in the `start_server.sh` script to match the actual location of your server.**

---

## 🚀 Server Launch Script

A helper script `start_server.sh` is included to build and run the server with the plugin and enable remote debugging via JDWP.

### `start_server.sh` contents:

```bash
#!/bin/bash
./gradlew clean build
cp build/libs/paws-of-the-forest-1.0.0-dev.jar ../Minecraft_server_paper_1.21.1/plugins/
cd ../Minecraft_server_paper_1.21.1
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar paper-1.21.1-231.jar nogui
```

> 🛠️ Make sure to adjust the server folder path in the script if yours differs.

---

## 🧭 Coming Soon

- Plugin configuration guide
- Permissions setup
- Gameplay mechanics overview

---

## 📄 License

See the [LICENSE](LICENSE) file for more details.
