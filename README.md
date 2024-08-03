ClientServerChat

*Implementation the basic mechanism of a client/server application based on Java—Sockets API*



# Task

You need to create two applications: socket-server and socket-client. Server shall support connecting a single client and be made as a separate Maven project. 

In this task, you need to implement the registration functionality. Example of the client operation:

Once you have implemented the application backbone, you should provide multi-user message exchange.

You need to modify the application so that it supports the following chat user life cycle:
1. Registration
2. Sign in (if no user is detected, close a connection)
3. Sending messages (each user connected to the server must receive a message)
4. Logout

Each message shall be saved in the database and contain the following information:
- Sender
- Message text
- Sending time

To make our application fully-featured, let's add the concept of "chatrooms" to it. Each chatroom can have a certain set of users. The chatroom contains a set of messages from participating users.

Each user can:
1.	Create a chatroom
2.	Choose a chatroom
3.	Send a message to a chatroom
4.	Leave a chatroom

When the user re-enters the application, 30 last messages shall be displayed in the room the user visited previously.

Using JSON format for message exchange will be a special task for you. In this way, each user command or message must be transferred to the server (and received from the server) in the form of a JSON line.

## Install

_before installing, make sure that you have Maven, Docker_

```bash
   git clone https://github.com/irunazushan/ClientServerChat_Java
   cd src/Chat
   # to start a server
   bash run-server.sh
   # to start a client
   bash run-client.sh
```

Then you can run several clients with command:
    java -jar target/socket-server.jar --port=8081

## Uninstall

```bash
    bash clean.sh
```
   
