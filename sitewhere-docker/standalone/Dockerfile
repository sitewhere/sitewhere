FROM ubuntu:14.04
RUN apt-get update
EXPOSE 8080 1883 61623

RUN apt-get install -y unzip wget openjdk-7-jdk

# Install HiveMQ
RUN cd /opt && wget --content-disposition http://www.hivemq.com/wp-content/uploads/hivemq-2.2.1.zip && unzip hivemq-2.2.1.zip
COPY configuration.properties /opt/hivemq-2.2.1/conf/configuration.properties
RUN ln -s /opt/hivemq-2.2.1 /opt/hivemq && useradd -d /opt/hivemq hivemq && chown -R hivemq.hivemq /opt/hivemq-2.2.1 && chown -R hivemq.hivemq /opt/hivemq && cd /opt/hivemq && chmod +x ./bin/run.sh

# Install MongoDB
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10 && echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list && apt-get update && apt-get install -y mongodb-org && mkdir -p /data/db

# Install SiteWhere
RUN cd /opt && wget --content-disposition https://s3.amazonaws.com/sitewhere/sitewhere-server-1.0.1.tar.gz && tar xvf sitewhere-server-1.0.1.tar.gz
ENV CATALINA_BASE /opt/sitewhere-server-1.0.1
RUN ln -s /opt/sitewhere-server-1.0.1 /opt/sitewhere && useradd -d /opt/sitewhere sitewhere && chown -R sitewhere.sitewhere /opt/sitewhere-server-1.0.1 && chown -R sitewhere.sitewhere /opt/sitewhere && cd /opt/sitewhere && chmod +x ./bin/*.sh 
CMD nohup /opt/hivemq/bin/run.sh > /dev/null & nohup /usr/bin/mongod > /dev/null & echo 'Waiting for MongoDB to start...' && while ! nc -vz localhost 27017 > /dev/null; do sleep 1; done && nohup /opt/sitewhere/bin/startup.sh & echo 'Starting SiteWhere...' && while ! [ -f /opt/sitewhere/logs/catalina.out ]; do sleep 1; done && tail -f /opt/sitewhere/logs/catalina.out
