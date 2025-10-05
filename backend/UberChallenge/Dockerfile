FROM eclipse-temurin:21-jdk-alpine
RUN mkdir -p /runner/source/
COPY ./ /runner/source/
RUN apk add python3 py3-pip maven
RUN python3 --version
RUN pip --version
WORKDIR /runner/source/
RUN pip i catboost
RUN pip i numpy
RUN pip i pandas
RUN pip i seaborn
RUN mvn clean package
RUN cp ./target/UberChallenge-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]