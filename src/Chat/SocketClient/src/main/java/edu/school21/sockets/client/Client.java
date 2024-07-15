package edu.school21.sockets.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket s ;
    private PrintWriter pw;
    private InputStreamReader in;
    private Scanner sc;
    private BufferedReader buff;

    public Client(Socket socket) {
        try {
            s = socket;
            in = new InputStreamReader(s.getInputStream());
            buff = new BufferedReader(in);
            pw = new PrintWriter(s.getOutputStream());
            sc = new Scanner(System.in);
        } catch (IOException e) {
            closeConnections();
        }
    }

    public void runClient() {
        listenForMessage();
        sendMessage();
    }

    private void sendMessage() {
        while (s.isConnected()) {
            String message = sc.nextLine();
            pw.println(message);
            pw.flush();
            if (message.equals("Exit")) {
                break;
            }
        }
        closeConnections();
    }



    private void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (s.isConnected()) {
                    try {
                        String message = buff.readLine();
                        if (message == null) {
                            closeConnections();
                            break;
                        }
                        System.out.println(message);
                    } catch (IOException e) {
                        closeConnections();
                        break;
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
