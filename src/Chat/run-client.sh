#build client
cd SocketClient
mvn clean package

#move to target folder
cd ..
mkdir -p target
mv SocketClient/target/socket-client-jar-with-dependencies.jar target/socket-client.jar

#run server
java -jar target/socket-client.jar --server-port=8081
