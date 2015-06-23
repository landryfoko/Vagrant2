export SRC="/var/libertas/git"
cwd=$(pwd)

killall screen
service mongod restart
echo "Starting Zookeeper"
screen -S zookeeper -d -m  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/bin/zookeeper-server-start.sh  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/config/zookeeper.properties
sleep 60
echo "Starting Kafka"
screen -S kafka -d -m  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/bin/kafka-server-start.sh  /usr/local/kafka/kafka_2.9.1-0.8.2-beta/config/server.properties


export MODULE="solr-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
\cp target/*.war /usr/local/solr/solr-4.10.2/example/webapps
\cp $cwd/*context.xml /usr/local/solr/solr-4.10.2/example/contexts


cd /usr/local/solr/solr-4.10.2/example
screen -S solr -d -m  java -Xmx128m -Dsolr.solr.home=/usr/local/solr/solr-4.10.2/example/example-schemaless/solr -Djetty.home=/usr/local/solr/solr-4.10.2/example/ -jar start.jar


export MODULE="config-server"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 
sleep 30

echo "Starting eureka"
export MODULE="eureka-server"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 

sleep 60
export MODULE="database-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="oauth-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 

export MODULE="creditcard-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="customer-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="device-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="entitlement-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="bookmark-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="genre-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="hls-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="hystrix-dashboard"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="ingestion-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="notification-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 



export MODULE="offer-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="playback-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="product-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="promotion-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="purchase-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="rating-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="recommendation-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="review-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 




export MODULE="tenant-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="turbine-server"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="watchhistory-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="workflow-microservice"
echo "Starting $MODULE"
cd $SRC/$MODULE
#screen -S $MODULE -d -m java -Xmx128m -jar target/*.jar 


export MODULE="zuul-server"
echo "Starting $MODULE"
cd $SRC/$MODULE
screen -S $MODULE -d -m java -Xmx512m -jar target/*.jar 

wget http://localhost:8983/solr-eurekaclient/eureka

echo '[{"name":"producers","stored":"true","type":"text_general"},{"name":"tags","stored":"true","type":"text_general"},{"name":"apiKey","stored":"true","type":"text_general"},{"name":"bindId","stored":"true","type":"text_general"},{"name":"offers","stored":"true","type":"text_general"},{"name":"title","stored":"true","type":"text_general"},{"name":"directors","stored":"true","type":"text_general"},{"name":"genres","stored":"true","type":"text_general"},{"name":"shorttitle","stored":"true","type":"text_general"},{"name":"releaseyear","stored":"true","type":"text_general"},{"name":"producttype","stored":"true","type":"text_general"},{"name":"blob","stored":"true","type":"text_general"},{"name":"actors","stored":"true","type":"text_general"},{"name":"binId","stored":"true","type":"text_general"}, {"name":"email","stored":"true","type":"text_general"}, {"name":"password","stored":"true","type":"text_general"}, {"name":"type","stored":"true","type":"text_general"}]' | curl -d @- http://localhost:8983/solr/schema/fields  -H 'Content-type:application/json'

