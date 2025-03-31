# Folosește ultima versiune de OpenJDK
FROM openjdk:21-jdk-slim

# Setăm variabila de mediu pentru a configura Java să deschidă portul de debug 5005
ENV JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,address=*:5005,server=y,suspend=n

# Copiem fișierul JAR construit pe sistemul local în imagine
COPY ./build/libs/hello-0.0.1-SNAPSHOT.jar /hello/libs/hello.jar

# Setăm directorul de lucru în interiorul imaginii
WORKDIR /hello/libs/

# Definim comanda pentru a rula aplicația
CMD ["java", "-jar", "/hello/libs/hello.jar"]
