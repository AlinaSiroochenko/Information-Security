package org.example.Clients;

import org.example.RSA.Signature;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Scanner;

public class Client1 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                Scanner in = new Scanner(System.in)
        ) {
            System.out.println("Connected to server!");
            System.out.println("Enter a message:");
            String message = in.nextLine();

            // Create an instance of Signature for working with RSA encryption
            Signature rsa = new Signature();
            String signature = rsa.sign(message);
            PublicKey publicKey = rsa.getPublicKey();

            // Create an output stream to send data through the socket
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            DataOutputStream dataStream = new DataOutputStream(outputStream);

            outputStream.writeObject(publicKey);
            dataStream.writeUTF(message);
            dataStream.writeUTF(signature);

            System.out.println("Data sent to the server.");
            outputStream.flush();
            dataStream.flush();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}