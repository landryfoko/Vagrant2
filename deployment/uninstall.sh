killall java
killall screen
service mongod stop
yum -y remove mongo*
rm -rf /usr/local/solr /usr/local/java /usr/local/kafka /usr/local/maven
rm -rf /etc/downloads
rm -rf /etc/yum.repos.d/mongodb.repo
rm -rf /var/libertas/
rm -rf /root/.m2
rm -rf /var/lib/yum/repos/x86_64/6/mongodb
find / -name '*mongo*' -exec rm -rf {} \;
find / -name '*java*' -exec rm -rf {} \;
rm -rf /etc/alternatives/java /usr/bin/java  /var/lib/alternatives/java
rm -rf /root/logs
rm -rf /root/solr-w*