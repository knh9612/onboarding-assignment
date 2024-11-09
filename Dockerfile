# 1. OpenJDK 베이스 이미지 사용
FROM openjdk:17-jdk

# 2. 작업 디렉토리 설정
WORKDIR /jwt

# 3. JAR 파일을 컨테이너로 복사
COPY build/libs/jwt-0.0.1-SNAPSHOT.jar /jwt/app.jar

# 4. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/jwt/app.jar"]