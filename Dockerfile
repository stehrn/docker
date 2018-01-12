FROM store/oracle/serverjre:8

ENV VERTICLE_FILE=docker-1.0-SNAPSHOT-fat.jar \
    VERTICLE_HOME=/usr/verticles \
    VERTICLE_CONFIG=application-conf.json

COPY target/$VERTICLE_FILE $VERTICLE_HOME/
COPY src/main/resources/application-conf.json $VERTICLE_HOME/

# Make port 80 available to the world outside this container
EXPOSE 8080

# Run vert.x when container starts
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE -conf $VERTICLE_CONFIG"]