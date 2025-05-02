# Используем официальный образ OpenJDK 21 в качестве базового
FROM openjdk:21-jdk

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы сборки в контейнер
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Собираем приложение без тестов
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "target/translation_gc-0.0.1-SNAPSHOT.jar"]
