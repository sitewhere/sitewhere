FROM ubuntu:14.04
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
EXPOSE 8080 5701

RUN apt-get install -y unzip wget openjdk-8-jdk

# Install SiteWhere
RUN cd /opt && wget --content-disposition https://s3.amazonaws.com/sitewhere/sitewhere-server-1.9.0.tgz && tar xvf sitewhere-server-1.9.0.tgz
ENV SITEWHERE_HOME="/opt/sitewhere-server-1.9.0"
RUN ln -s /opt/sitewhere-server-1.9.0 /opt/sitewhere && useradd -d /opt/sitewhere sitewhere && chown -R sitewhere.sitewhere /opt/sitewhere-server-1.9.0 && chown -R sitewhere.sitewhere /opt/sitewhere && cd /opt/sitewhere && chmod +x /opt/sitewhere-server-1.9.0/bin/*.sh
CMD nohup /opt/sitewhere/bin/startup.sh & echo 'Starting SiteWhere...' && while ! [ -f /opt/sitewhere/logs/sitewhere.log ]; do sleep 1; done && tail -f /opt/sitewhere/logs/sitewhere.log
