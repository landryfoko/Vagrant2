set OLDDIR=%CD%
D:
cd G:\Puissance\solr-5.2.1\example

start call "cmd /c java -Dsolr.solr.home=example-schemaless/solr   -Xms1024m -Xmx1024m -XX:PermSize=256M -XX:MaxPermSize=512M -Dgroovy.target.indy=true -Dlibertas.current.profile=pascal -Dlibertas.database.prefix=pascal -jar start.jar"


cd G:\Puissance\kafka_2.9.1-0.8.2.1\bin\windows

start G:\Puissance\kafka_2.9.1-0.8.2.1\bin\windows\zookeeper-server-start.bat G:\Puissance\kafka_2.9.1-0.8.2.1\config\zookeeper.properties


sleep 10



cd G:\Puissance\kafka_2.9.1-0.8.2.1\bin\windows

start G:\Puissance\kafka_2.9.1-0.8.2.1\bin\windows\kafka-server-start.bat G:\Puissance\kafka_2.9.1-0.8.2.1\config\server.properties


C:

cd "C:\Program Files\MongoDB\Server\3.0\bin"

start call "cmd /c mongod.exe --dbpath=C:\data"

D:
chdir /d %OLDDIR%