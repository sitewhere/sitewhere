FROM ubuntu:14.04
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
EXPOSE 1883 61623

RUN apt-get install -y unzip wget openjdk-8-jdk

# Install HiveMQ
RUN cd /opt && wget --content-disposition https://s3.amazonaws.com/sitewhere-hivemq/hivemq-3.0.2.zip && unzip hivemq-3.0.2.zip
COPY config.xml /opt/hivemq-3.0.2/conf/config.xml
RUN ln -s /opt/hivemq-3.0.2 /opt/hivemq && useradd -d /opt/hivemq hivemq && chown -R hivemq.hivemq /opt/hivemq-3.0.2 && chown -R hivemq.hivemq /opt/hivemq && cd /opt/hivemq && chmod +x ./bin/run.sh

CMD /opt/hivemq/bin/run.sh
