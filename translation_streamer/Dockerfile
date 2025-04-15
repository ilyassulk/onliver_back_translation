# Используем официальный образ OpenJDK 21 в качестве базового
FROM openjdk:21-jdk

# Устанавливаем необходимые пакеты GStreamer
RUN apt-get update && apt-get install -y \
    libgstreamer1.0-0 \
    libgstreamer-plugins-base1.0-0 \
    gstreamer1.0-tools \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
 && rm -rf /var/lib/apt/lists/*

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы сборки в контейнер
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Даём права на выполнение Maven Wrapper и собираем приложение (без тестов)
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Задаём переменную окружения для поиска нативных библиотек GStreamer
ENV LD_LIBRARY_PATH=/usr/lib/x86_64-linux-gnu/gstreamer-1.0:$LD_LIBRARY_PATH

# Указываем команду запуска. Параметр -Djna.library.path указывает JVM, где искать нативные библиотеки.
ENTRYPOINT ["java", "-Djna.library.path=/usr/lib/x86_64-linux-gnu/gstreamer-1.0", "-jar", "target/translation_streamer-0.0.1-SNAPSHOT.jar"]
