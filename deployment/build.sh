rm -rf /var/libertas/git
git clone -b master  https://fenkam:man2than4..@github.com/LibertasNet/renaissance.git /var/libertas/git/

source /etc/environment
export SRC="/var/libertas/git"
killall screen


cd $SRC/camel-components/camel-aws-transcoder
mvn clean package install  -DskipTests=true

export MODULE="camel-headers"

cd $SRC/camel-components/$MODULE
mvn clean package install  -DskipTests=true


export MODULE="camel-braintree"
cd $SRC/camel-components/$MODULE
mvn clean package install  -DskipTests=true

export MODULE="camel-azure-ms"
cd $SRC/camel-components/$MODULE
mvn clean package install  -DskipTests=true



export MODULE="camel-dropbox"
cd $SRC/camel-components/$MODULE
mvn clean package install  -DskipTests=true

export MODULE="camel-stack"
cd $SRC/camel-components/$MODULE
mvn clean package install  -DskipTests=true

export MODULE="camel-restlet"
cd $SRC/camel-components/$MODULE
mvn clean package install  -DskipTests=true

export MODULE="camel-validate"
cd $SRC/camel-components/$MODULE
mvn clean package install  -DskipTests=true

export MODULE="lib-parent"
cd $SRC/$MODULE
mvn clean package install  -DskipTests

export MODULE="ms-parent"
cd $SRC/$MODULE
mvn clean package install  -DskipTests


export MODULE="solr-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests=true


export MODULE="common"
cd $SRC/$MODULE
mvn clean package install  -DskipTests


export MODULE="config-server"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 

export MODULE="eureka-server"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 

export MODULE="database-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="oauth-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 

export MODULE="creditcard-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 

export MODULE="bookmark-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="customer-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="device-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="entitlement-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="genre-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="hls-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="hystrix-dashboard"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="ingestion-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="notification-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 



export MODULE="offer-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="playback-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="product-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="promotion-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="purchase-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="rating-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="recommendation-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="review-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests


export MODULE="tenant-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="turbine-server"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="watchhistory-microservice"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
 


export MODULE="workflow-microservice"
cd $SRC/$MODULE
#mvn clean package install  -DskipTests
 


export MODULE="zuul-server"
cd $SRC/$MODULE
mvn clean package install  -DskipTests
