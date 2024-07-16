#run postgreSQL db with docker
docker build -t chat-postgres-db .
docker run -d --name chat-postgres-container -p 5432:5432 chat-postgres-db

# To check connection use:
# psql -h localhost -U postgres -d database

#build server
cd SocketServer
mvn clean package

#move to target folder
cd ..
mkdir -p target
mv ./SocketServer/

#run client
java -jar target/socket-server.jar --port=8081
