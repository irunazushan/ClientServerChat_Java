#build server
cd SocketServer
mvn clean package

#move to target folder
cd ..
mkdir -p target
mv SocketServer/target/socket-server-jar-with-dependencies.jar target/socket-server.jar

#run client
java -jar target/socket-server.jar --port=8081
