package edu.school21.sockets.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private Socket s;
    private PrintWriter pw;
    private InputStreamReader in;
    private Scanner sc;
    private BufferedReader buff;
    private JsonConverter jc;
    private Long userId = 0L;
    private Long currentRoomId = 0L;
    private AtomicBoolean isOnline = new AtomicBoolean(false);


    public Client(Socket socket) {
        try {
            s = socket;
            in = new InputStreamReader(s.getInputStream());
            buff = new BufferedReader(in);
            pw = new PrintWriter(s.getOutputStream());
            sc = new Scanner(System.in);
            jc = new JsonConverter();
            setOnline(true);
        } catch (IOException e) {
            closeConnections();
        }
    }

    public boolean isOnline() {
        return isOnline.get();
    }

    public void setOnline(boolean online) {
        isOnline.set(online);
    }

    public void runClient() {
        listenForMessage();
        sendMessage();
    }

    private void sendMessage() {
        while (isOnline() && s.isConnected()) {
            String message;
            try {
                message = sc.nextLine();
            } catch (IllegalStateException e) {
                break;
            }

            String jsonMessage = jc.convertMessageToJson(message, userId, currentRoomId);
            pw.println(jsonMessage);
            pw.flush();
            if (Objects.equals(message, "/close_connection")) {
                setOnline(false);
            }
        }
        closeConnections();
    }


    private void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOnline() && s.isConnected()) {
                    try {
                        String jsonMessage = buff.readLine();
                        if (isOnline() && jsonMessage != null) {
                            String message = jc.getMessageFromJson(jsonMessage);
                            System.out.println(message);
                        } else {
                            throw new IOException();
                        }
                    } catch (IOException e) {
                        closeConnections();
                        setOnline(false);
                    }
                }
            }
        }).start();
    }

    private void closeConnections() {
        try {
            if (s != null) {
                s.close();
            }
            if (sc != null) {
                sc.close();
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
