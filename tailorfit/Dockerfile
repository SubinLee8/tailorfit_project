FROM openjdk:21-jdk-slim
WORKDIR /app  # 컨테이너 내부에서 작업할 디렉토리 설정
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
