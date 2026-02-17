FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY Travelio/ ./Travelio/

WORKDIR /app/Travelio

RUN chmod +x ./gradlew && ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/Travelio/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
