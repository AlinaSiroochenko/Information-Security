package org.example.Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, waiting for connections...");

            try (Socket client1Socket = serverSocket.accept();
                 Socket client2Socket = serverSocket.accept()) {

                System.out.println("Client 1 connected");
                System.out.println("Client 2 connected");

                Thread client1Thread = new Thread(new ClientHandler(client1Socket, client2Socket));
                Thread client2Thread = new Thread(new ClientHandler(client2Socket, client1Socket));

                client1Thread.start();
                client2Thread.start();

                client1Thread.join();
                client2Thread.join();
            }
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}