package edu.school21.sockets.server;

import edu.school21.sockets.config.SocketsApplicationConfig;
import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import edu.school21.sockets.services.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private Socket s;
    private PrintWriter pw;
    private InputStreamReader in;
    private BufferedReader buff;
    private Chatroom currentRoom;
    private User currentUser;
    private UsersService us;
    private JsonConverter jc;

    public ClientHandler(Socket socket) {
        try {
            s = socket;
            clients.add(this);
            in = new InputStreamReader(s.getInputStream());
            pw = new PrintWriter(s.getOutputStream());
            buff = new BufferedReader(in);
            ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
            us = context.getBean("usersServiceImpl", UsersService.class);
            jc = new JsonConverter();
        } catch (IOException e) {
            closeConnections();
        }
    }

    @Override
    public void run() {
        pw.println(jc.sendMessageFromServer("Hello from Server!"));
        while (s.isConnected()) {
            printMenuBar();
            try {
                runClientHandler();
            } catch (IOException | NullPointerException | IllegalArgumentException e) {
                closeConnections();
                break;
            }
        }
    }

    private void runClientHandler() throws IOException, NullPointerException {
        String jsonMessage = buff.readLine();
        String message = jc.getMessageFromJson(jsonMessage);
        switch (message) {
            case "1":
                signInUser();
                break;
            case "2":
                signUpUser();
                break;
            case "3":
                logOutUser();
                break;
            case "4":
                exitFromServer();
                break;
        }
    }

    private void signInUser() throws IOException {
        if (currentUser == null) {
            String[] nameAndPassword = getNameAndPassword();
            currentUser = us.signIn(nameAndPassword[0], nameAndPassword[1]);
        }

        if (currentUser != null) {
            openChatroomMenu();
        } else {
            pw.println(jc.sendMessageFromServer("Incorrect login or password"));
            pw.flush();
        }
    }

    private void signUpUser() throws IOException {
        String[] nameAndPassword = getNameAndPassword();

        ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
        us = context.getBean("usersServiceImpl", UsersService.class);
        us.signUp(nameAndPassword[0], nameAndPassword[1]);

        System.out.println("User with name: " + nameAndPassword[0] + " is added to DB");
        pw.println(jc.sendMessageFromServer("Successful!"));
        pw.flush();
    }

    private void logOutUser() {
        if (currentUser != null) {
            pw.println(jc.sendMessageFromServer("You have left from account: " + currentUser.getName()));
            pw.flush();
            currentUser = null;
        } else {
            pw.println(jc.sendMessageFromServer("You aren't logged in to log out"));
            pw.flush();
        }
    }

    private void exitFromServer() {
        pw.println(jc.sendMessageFromServer("You have left from the server."));
        pw.flush();
        closeConnections();
    }

    private String[] getNameAndPassword() throws IOException {
        String[] namePassword = new String[2];
        pw.println(jc.sendMessageFromServer("Enter username:"));
        pw.flush();
        String jsonMessage = buff.readLine();
        namePassword[0] = jc.getMessageFromJson(jsonMessage);

        pw.println(jc.sendMessageFromServer("Enter password:"));
        pw.flush();
        jsonMessage = buff.readLine();
        namePassword[1] = jc.getMessageFromJson(jsonMessage);
        return namePassword;
    }

    private void openChatroomMenu() throws IOException {
        printChatroomMenu();
        String jsonMessage = buff.readLine();
        String reply = jc.getMessageFromJson(jsonMessage);
        if (reply.equals("1")) {
            createChatroomByUser();
        } else if (reply.equals("2")) {
            enterToChatroom();
        } else if (reply.equals("3")) {
            pw.println(jc.sendMessageFromServer("To main menu"));
            pw.flush();
        }
    }

    private void createChatroomByUser() throws IOException {
        pw.println(jc.sendMessageFromServer("Enter the name of new chatroom:"));
        pw.flush();

        String jsonMessage = buff.readLine();
        String str = jc.getMessageFromJson(jsonMessage);

        us.createChatroom(str, currentUser);
        pw.println(jc.sendMessageFromServer("New chatroom '" + str + "' is created"));
        pw.flush();
        enterToChatroom();
    }

    private void enterToChatroom() throws IOException {
        setCurrentChatroom();

        if (currentRoom != null) {
            showLast30Messages(currentRoom);
            startChatting();
        }
    }

    private void setCurrentChatroom() throws IOException {
        StringBuilder str = new StringBuilder("Rooms:\n");
        int index = 1;
        List<Chatroom> chatrooms = us.chooseChatroom();

        for (Chatroom chatroom : chatrooms) {
            str.append(index++).append(". ").append(chatroom.getName()).append("\n");
        }
        str.append(index).append(". ").append("Exit");
        pw.println(jc.sendMessageFromServer(str.toString()));
        pw.flush();

        String jsonMessage = buff.readLine();
        String reply = jc.getMessageFromJson(jsonMessage);

        int chosenRoomId = Integer.parseInt(reply);
        try {
            currentRoom = chatrooms.get(chosenRoomId - 1);
        } catch (IndexOutOfBoundsException e) {
            pw.println(jc.sendMessageFromServer("There is no such room with number: " + chosenRoomId));
            pw.flush();
        }
    }

    private void startChatting() throws IOException {
        String jsonMessage = jc.convertMessageToJson(currentUser.getName() + " entered to the chat!", currentUser.getId(), currentRoom.getId());
        broadcastMessage(jsonMessage);
        jsonMessage = buff.readLine();
        String message = jc.getMessageFromJson(jsonMessage);
        while (!doesUserExit(message)) {
            us.sendMessage(currentUser, currentRoom, message);
            jsonMessage = jc.convertMessageToJson(currentUser.getName() + ": " + message, currentUser.getId(), currentRoom.getId());
            broadcastMessage(jsonMessage);
            jsonMessage = buff.readLine();
            message = jc.getMessageFromJson(jsonMessage);
        }
        jsonMessage = jc.convertMessageToJson(currentUser.getName() + " left from chat!", currentUser.getId(), currentRoom.getId());
        broadcastMessage(jsonMessage);

        pw.println(jc.sendMessageFromServer("You have left the chat."));
        pw.flush();
        currentRoom = null;
    }

    private boolean doesUserExit(String message) {
        return Objects.equals(message, "Exit") || Objects.equals(message, "/close_connection");
    }

    private void showLast30Messages(Chatroom chatroom) {
        StringBuilder lastMessages = new StringBuilder(chatroom.getName() + " ---\n");
        List<Message> messages = chatroom.getMessages();
        int size = messages.size();
        int startIndex = 0;
        if (size > 30) {
            startIndex = size - 30;
        }
        for (int i = startIndex; i < size; i++) {
            Message msg = messages.get(i);
            lastMessages.append(msg.getSender().getName()).append(": ").append(msg.getMessage());
            if (i < size - 1) {
                lastMessages.append("\n");
            }
        }
        pw.println(jc.sendMessageFromServer(lastMessages.toString()));
        pw.flush();
    }

    private void printMenuBar() {
        String menuBar = "USER: " + (currentUser != null ? currentUser.getName() : "not logged") + "\n" +
                (currentUser != null ? "1. ChatroomMenu" : "1. SignIn") + "\n" +
                "2. SignUp\n" +
                "3. LogOut\n" +
                "4. Exit";
        pw.println(jc.sendMessageFromServer(menuBar));
        pw.flush();
    }

    private void printChatroomMenu() {
        pw.println(jc.sendMessageFromServer("1. Create room\n" +
                "2. Choose room\n" +
                "3. Exit"));
        pw.flush();
    }

    private void broadcastMessage(String message) {
        for (ClientHandler ch : clients) {
            if (Objects.equals(ch.currentRoom, currentRoom)) {
                ch.pw.println(message);
                ch.pw.flush();
            }
        }
    }

    private void closeConnections() {
        clients.remove(this);
        try {
            if (s != null) {
                s.close();
            }
            if (in != null) {
                in.close();
            }
            if (pw != null) {
                pw.close();
            }
            if (buff != null) {
                buff.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
