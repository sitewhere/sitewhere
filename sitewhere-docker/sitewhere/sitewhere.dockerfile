FROM ubuntu:14.10
RUN apt-get update
EXPOSE 8080

RUN apt-get install -y unzip wget openjdk-7-jdk

RUN cd /opt && wget --content-disposition https://s3.amazonaws.com/sitewhere/sitewhere-server-1.0.3.zip && unzip sitewhere-server-1.0.3.zip && echo "1"
ENV CATALINA_BASE="/opt/sitewhere-server-1.0.3" CATALINA_HOME="/opt/sitewhere-server-1.0.3"
COPY sitewhere-server.xml /opt/sitewhere-server-1.0.3/conf/sitewhere/sitewhere-server.xml
RUN ln -s /opt/sitewhere-server-1.0.3 /opt/sitewhere && useradd -d /opt/sitewhere sitewhere && chown -R sitewhere.sitewhere /opt/sitewhere-server-1.0.3 && chown -R sitewhere.sitewhere /opt/sitewhere && cd /opt/sitewhere && chmod +x /opt/sitewhere-server-1.0.3/bin/*.sh 
CMD nohup /opt/sitewhere/bin/startup.sh & echo 'Starting SiteWhere...' && while ! [ -f /opt/sitewhere/logs/catalina.out ]; do sleep 1; done && tail -f /opt/sitewhere/logs/catalina.out
