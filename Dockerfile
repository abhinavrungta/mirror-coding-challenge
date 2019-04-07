FROM openjdk:8-jre
USER root
EXPOSE 9000
ADD target/universal/mirror-coding-challenge-1.0-SNAPSHOT.zip ./
RUN set -x && unzip -d svc mirror-coding-challenge-1.0-SNAPSHOT.zip && mv svc/*/* svc/ && rm svc/bin/*.bat && mv svc/bin/* svc/bin/start
CMD /svc/bin/start