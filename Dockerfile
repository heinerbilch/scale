FROM eclipse-temurin:21-alpine
ARG USER_UID=1000
ARG GROUP_GID=1000
ARG UGNAME=webapp
RUN addgroup --system --gid ${GROUP_GID} ${UGNAME}
RUN adduser --system --disabled-password --home /home/${UGNAME} \
    --uid ${USER_UID} --ingroup ${UGNAME} ${UGNAME}
USER ${UGNAME}:${UGNAME}
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]