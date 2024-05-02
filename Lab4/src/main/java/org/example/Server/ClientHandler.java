package org.example.Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket incomingSocket;
    private final Socket outgoingSocket;

    public ClientHandler(Socket incomingSocket, Socket outgoingSocket) {
        this.incomingSocket = incomingSocket;
        this.outgoingSocket = outgoingSocket;
    }

    @Override
    public void run() {
        try {
            // Create streams to read objects and primitive data from the incoming client
            ObjectInputStream inputStream = new ObjectInputStream(incomingSocket.getInputStream());
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            // ... to the outgoing client
            ObjectOutputStream outputStream = new ObjectOutputStream(outgoingSocket.getOutputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // Read the public key, message, and digital signature from the incoming client
            Object publicKey = inputStream.readObject();
            String message = dataInputStream.readUTF();
            String digitalSignature = dataInputStream.readUTF();

            outputStream.writeObject(publicKey);
            dataOutputStream.writeUTF(message);
            dataOutputStream.writeUTF(digitalSignature);

            outputStream.flush();
            dataOutputStream.flush();
        } catch (Exception e) {
            System.err.println("Error in ClientHandler: " + e.getMessage());
        }
    }
}