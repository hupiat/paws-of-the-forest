#!/bin/bash
mvn clean package
cp target/paws-of-the-forest-1.0.0.jar ../Minecraft_server_paper_1.21.4/plugins/
cd ../Minecraft_server_paper_1.21.4
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar paper-1.21.4-231.jar nogui
