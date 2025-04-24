# 1. В образе openjdk:21-jdk-slim уже есть apt-get  ﹙Debian Bullseye﹚
FROM --platform=linux/amd64 openjdk:21-jdk-slim

# 2. Ставим GStreamer и чистим кеш
RUN apt-get update && apt-get install -y \
        libgstreamer1.0-0 \
        libgstreamer-plugins-base1.0-0 \
        gstreamer1.0-tools \
        gstreamer1.0-plugins-base \
        gstreamer1.0-plugins-good \
        gstreamer1.0-plugins-bad \
        gstreamer1.0-plugins-ugly \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# 3. Работаем как прежде
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw \
 && ./mvnw clean package -DskipTests

# 4. LD_LIBRARY_PATH задаём безопасно: ${LD_LIBRARY_PATH:-} → «пусто, если не было»
ENV LD_LIBRARY_PATH="/usr/lib/x86_64-linux-gnu/gstreamer-1.0:${LD_LIBRARY_PATH:-}"

ENTRYPOINT ["java","-Djna.library.path=/usr/lib/x86_64-linux-gnu/gstreamer-1.0","-jar","target/translation_streamer-0.0.1-SNAPSHOT.jar"]
