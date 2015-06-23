 
 #Configuration parameters
 
 
 yum -y install unzip wget wget nano screen gcc libevent libevent-devel git dos2unix psmisc
 
 chkconfig iptables off
 service iptables stop
 
 dos2unix solr-eurekaclient-jetty.context.xml
 
 
 mkdir -p /etc/downloads  /usr/local/java  /usr/local/kafka  /usr/local/solr /var/libertas/git /usr/local/maven
 
 wget -N -S http://apache.parentingamerica.com/kafka/0.8.2-beta/kafka_2.9.1-0.8.2-beta.tgz -P /etc/downloads
 wget -N -S http://mirror.its.dal.ca/apache/lucene/solr/4.10.2/solr-4.10.2.zip -P /etc/downloads
 wget -N -S --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u5-b13/jdk-8u5-linux-x64.tar.gz -P /etc/downloads
 wget -N -S http://mirror.csclub.uwaterloo.ca/apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz -P /etc/downloads
 
 
 tar zxf /etc/downloads/jdk-8u5-linux-x64.tar.gz  -C /usr/local/java
 update-alternatives --install /usr/bin/java java /usr/local/java/jdk1.8.0_05/jre/bin/java 1; 
 update-alternatives --set java /usr/local/java/jdk1.8.0_05/jre/bin/java
 
 
 unzip /etc/downloads/solr-4.10.2.zip -d /usr/local/solr/
 cp ./solr-eurekaclient-jetty-context.xml  /usr/local/solr/solr-4.10.2/example/contexts
 
 tar zxf /etc/downloads/apache-maven-3.2.5-bin.tar.gz -C /usr/local/maven
 echo "
 export JAVA_HOME=/usr/local/java/jdk1.8.0_05/
 export PATH=:/usr/local/maven/apache-maven-3.2.5/bin:$PATH
 " > /etc/environment
 source /etc/environment
 
tar zxf /etc/downloads/kafka_2.9.1-0.8.2-beta.tgz  -C /usr/local/kafka
screen -S zookeeper -d -m  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/bin/zookeeper-server-start.sh  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/config/zookeeper.properties
screen -S kafka -d -m  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/bin/kafka-server-start.sh  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/config/server.properties
screen -S solr -d -m  java -Xmx256m -Dsolr.solr.home=/usr/local/solr/solr-4.10.2/example/example-schemaless/solr -Djetty.home=/usr/local/solr/solr-4.10.2/example/ -jar /usr/local/solr/solr-4.10.2/example/start.jar

 touch /etc/yum.repos.d/mongodb.repo
 echo '[mongodb]' >> /etc/yum.repos.d/mongodb.repo
 echo 'name=MongoDB Repository' >> /etc/yum.repos.d/mongodb.repo
 echo 'baseurl=http://downloads-distro.mongodb.org/repo/redhat/os/x86_64/' >> /etc/yum.repos.d/mongodb.repo
 echo 'gpgcheck=0' >> /etc/yum.repos.d/mongodb.repo
 echo 'enabled=1' >> /etc/yum.repos.d/mongodb.repo
 
 yum install -y mongodb-org
 echo 'SELINUX=disabled' > /etc/selinux.conf
 
