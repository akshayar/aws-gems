FROM openjdk:11
WORKDIR /app


COPY target/eks-sample-ws-1.0.0.jar /app/app.jar
COPY src/main/config /app/config
USER root
ENV STREAM_NAME=aksh-first
ENV REGION=us-east-1
ENTRYPOINT ["java", "-jar" ,"-DstreamName=${STREAM_NAME}","-Dregion=${REGION}" ,"/app/app.jar", "--spring.config.location=/app/config/application.properties"]
