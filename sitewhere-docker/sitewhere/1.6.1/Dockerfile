FROM ubuntu:14.04
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:openjdk-r/ppa
RUN apt-get update
EXPOSE 8080

RUN apt-get install -y unzip wget openjdk-8-jdk

# Install SiteWhere
RUN cd /opt && wget --content-disposition https://s3.amazonaws.com/sitewhere/sitewhere-server-1.6.1.zip && unzip sitewhere-server-1.6.1.zip
ENV CATALINA_BASE="/opt/sitewhere-server-1.6.1" CATALINA_HOME="/opt/sitewhere-server-1.6.1"
RUN ln -s /opt/sitewhere-server-1.6.1 /opt/sitewhere && useradd -d /opt/sitewhere sitewhere && chown -R sitewhere.sitewhere /opt/sitewhere-server-1.6.1 && chown -R sitewhere.sitewhere /opt/sitewhere && cd /opt/sitewhere && chmod +x /opt/sitewhere-server-1.6.1/bin/*.sh
