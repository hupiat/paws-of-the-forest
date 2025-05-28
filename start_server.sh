#!/bin/bash
./gradlew clean build
cp build/libs/paws-of-the-forest-1.0.0-dev.jar ../Minecraft_server_paper_1.21.1/plugins/
cd ../Minecraft_server_paper_1.21.1
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar paper-1.21.1-133.jar nogui
