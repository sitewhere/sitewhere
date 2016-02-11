FROM ubuntu:14.04
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
EXPOSE 8080 1883 61623

RUN apt-get install -y unzip wget openjdk-8-jdk

# Install HiveMQ
RUN cd /opt && wget --content-disposition https://s3.amazonaws.com/sitewhere-hivemq/hivemq-3.0.2.zip && unzip hivemq-3.0.2.zip
COPY config.xml /opt/hivemq-3.0.2/conf/config.xml
RUN ln -s /opt/hivemq-3.0.2 /opt/hivemq && useradd -d /opt/hivemq hivemq && chown -R hivemq.hivemq /opt/hivemq-3.0.2 && chown -R hivemq.hivemq /opt/hivemq && cd /opt/hivemq && chmod +x ./bin/run.sh

# Install MongoDB
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10 && echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list && apt-get update && apt-get install -y mongodb-org && mkdir -p /data/db

# Install SiteWhere
RUN cd /opt && wget --content-disposition https://s3.amazonaws.com/sitewhere/sitewhere-server-1.6.0.zip && unzip sitewhere-server-1.6.0.zip
ENV CATALINA_BASE="/opt/sitewhere-server-1.6.0" CATALINA_HOME="/opt/sitewhere-server-1.6.0"
RUN ln -s /opt/sitewhere-server-1.6.0 /opt/sitewhere && useradd -d /opt/sitewhere sitewhere && chown -R sitewhere.sitewhere /opt/sitewhere-server-1.6.0 && chown -R sitewhere.sitewhere /opt/sitewhere && cd /opt/sitewhere && chmod +x /opt/sitewhere-server-1.6.0/bin/*.sh
CMD nohup /opt/hivemq/bin/run.sh > /dev/null & nohup /usr/bin/mongod --smallfiles > /dev/null & echo 'Waiting for MongoDB to start...' && while ! nc -vz localhost 27017 2> /dev/null; do sleep 1; done && nohup /opt/sitewhere/bin/startup.sh & echo 'Starting SiteWhere...' && while ! [ -f /opt/sitewhere/logs/catalina.out ]; do sleep 1; done && tail -f /opt/sitewhere/logs/catalina.out
