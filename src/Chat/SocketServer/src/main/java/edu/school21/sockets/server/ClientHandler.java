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
import java.util.ArrayList;
import java.util.List;
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

    ClientHandler(Socket socket) {
        try {
            s = socket;
            clients.add(this);
            in = new InputStreamReader(s.getInputStream());
            pw = new PrintWriter(s.getOutputStream());
            buff = new BufferedReader(in);
            ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
            us = context.getBean("usersServiceImpl", UsersService.class);
        } catch (IOException e) {
            closeConnections();
        }
    }

    @Override
    public void run() {
        pw.println("Hello from Server!");
        while (s.isConnected()) {
            printMenuBar();
            try {
                runClientHandler();
            } catch (IOException e) {
                closeConnections();
                break;
            }
        }
    }

    private void printMenuBar() {
        pw.println("USER: " + (currentUser != null ? currentUser.getName() : "not logged"));
        pw.println("1. signIn\n" +
                "2. SignUp\n" +
                "3. LogOut\n" +
                "4. Exit");
        pw.flush();
    }

    private void runClientHandler() throws IOException {
        String str = buff.readLine();
        switch (str) {
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

    private void signUpUser() throws IOException {
        pw.println("Enter username:");
        pw.flush();
        String name = buff.readLine();

        pw.println("Enter password:");
        pw.flush();
        String password = buff.readLine();

        ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
        us = context.getBean("usersServiceImpl", UsersService.class);
        us.signUp(name, password);

        System.out.println("User with name: " + name + " is added to DB");
        pw.println("Successful!");
        pw.flush();
    }

    private void logOutUser() {
        if (currentUser != null) {
            pw.println("You have left from account: " + currentUser.getName());
            pw.flush();
            currentUser = null;
        } else {
            pw.println("You aren't logged in to log out");
            pw.flush();
        }
    }

    private void signInUser() throws IOException {
        if (currentUser == null) {
            pw.println("Enter username:");
            pw.flush();
            String name = buff.readLine();

            pw.println("Enter password:");
            pw.flush();
            String password = buff.readLine();
            currentUser = us.signIn(name, password);
        } else {
            pw.println("You have already logged in:");
            pw.flush();
        }
        if (currentUser != null) {
            printRoomMenu();
            String str = buff.readLine();
            if (str.equals("1")) {
//                pw.println("Enter the name of new chatroom:");
//                pw.flush();
//                str = buff.readLine();
//                us.createChatroom(str, currentUser);
//                pw.println("New chatroom '" + str + "' is created");
//                pw.flush();
                createChatroomByUser();
            } else if (str.equals("2")) {
                chooseChatRoomAndMessage();
            } else if (str.equals("3")) {
                pw.println("To main menu");
                pw.flush();
            }
        } else {
            pw.println("Incorrect login or password");
            pw.flush();
        }
    }

    private void createChatroomByUser() throws IOException {
        pw.println("Enter the name of new chatroom:");
        pw.flush();
        String str = buff.readLine();
        us.createChatroom(str, currentUser);
        pw.println("New chatroom '" + str + "' is created");
        pw.flush();
        chooseChatRoomAndMessage();
    }
    private void chooseChatRoomAndMessage() throws IOException {
        List<Chatroom> chatrooms = us.chooseChatroom();
        pw.println("Rooms:");
        int index = 1;
        for (Chatroom chatroom : chatrooms) {
            pw.println(index++ + ". " + chatroom.getName());
        }
        pw.println(index + ". " + "Exit");
        pw.flush();
        String str = buff.readLine();
        int chosenRoomId = Integer.parseInt(str);
        try {
            currentRoom = chatrooms.get(chosenRoomId - 1);
        } catch (IndexOutOfBoundsException e) {
            pw.println("There is no such room with number: " + chosenRoomId);
            pw.flush();
        }
        if (currentRoom != null) {
            showLast30Messages(currentRoom);
            String message = buff.readLine();
            while (!message.equals("Exit")) {
                us.sendMessage(currentUser, currentRoom, message);
                broadcastMessage(currentUser.getName() + ": " + message);
                message = buff.readLine();
            }
            broadcastMessage(currentUser.getName() + " left from chat!");
            pw.println("You have left the chat.");
            pw.flush();
            currentRoom = null;
        }
    }

    private void showLast30Messages(Chatroom chatroom) {
        pw.println(chatroom.getName() + " ---");
        List<Message> messages = chatroom.getMessages();
        int size = messages.size();
        int startIndex = 0;
        if (size > 30) {
            startIndex = size - 30;
        }
        for (int i = startIndex; i < size; i++) {
            Message msg = messages.get(i);
            pw.println(msg.getSender().getName() + ": " + msg.getMessage());
        }
        pw.flush();
    }

    private void printRoomMenu() {
        pw.println("1. Create room\n" +
                "2. Choose room\n" +
                "3. Exit");
        pw.flush();
    }

    private void exitFromServer() {
        pw.println("You have left the chat.");
        pw.flush();
        closeConnections();
    }

    private void broadcastMessage(String message) {
        for (ClientHandler ch : clients) {
            if (ch.currentRoom.equals(currentRoom)) {
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
