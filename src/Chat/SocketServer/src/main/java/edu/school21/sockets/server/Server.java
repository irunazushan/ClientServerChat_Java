package edu.school21.sockets.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket ss;

    public Server(ServerSocket ss) throws IOException {
        this.ss = ss;
    }

    public void runServer() {

        try {
            while (!ss.isClosed()) {

                Socket socket = ss.accept();
                System.out.println("A new client has connected!");
                ClientHandler ch = new ClientHandler(socket);

                Thread thread = new Thread(ch);
                thread.start();
            }
        } catch (IOException e) {
            closeServer();
        }
    }

    private void closeServer() {
        try {
            if (ss != null) {
                ss.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}