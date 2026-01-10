# Java 17-jdk-jammy 이미지를 기반으로 합니다.
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너로 복사합니다.
# build/libs/*.jar 패턴을 사용하여 파일 이름이 변경되어도 적용되도록 합니다.
COPY build/libs/*.jar app.jar

# 애플리케이션 포트를 8080으로 노출합니다.
EXPOSE 8080

# 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]