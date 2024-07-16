package edu.school21.sockets.app;

import edu.school21.sockets.server.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    private static final String PORT_PREFIX = "--port=";
    private static final int INVALID_PORT = -1;

    public static void main(String[] args) throws IOException {
        int port = getPort(args);
        if (port == INVALID_PORT) {
            throw new RuntimeException("Invalid arguments are given");
        }

        ServerSocket ss = new ServerSocket(port);
        try {
            Server server = new Server(ss);
            server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getPort(String[] args) {
        int port = INVALID_PORT;
        if (args.length == 1 && args[0].startsWith(PORT_PREFIX)) {
            port = Integer.parseInt(args[0].substring(PORT_PREFIX.length()));
        }
        return port;
    }

}
