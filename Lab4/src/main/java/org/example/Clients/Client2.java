package org.example.Clients;

import org.example.RSA.Signature;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

public class Client2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to server!");

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            DataInputStream dataStream = new DataInputStream(inputStream);

            // Read data sent from the server
            PublicKey publicKey = (PublicKey) inputStream.readObject();
            String message = dataStream.readUTF();
            String digitalSignature = dataStream.readUTF();

            System.out.println("Received digital signature: " + digitalSignature);

            System.out.println("Do you want to modify the digital signature? (Yes/No)");
            String response = scanner.nextLine().trim().toLowerCase();
            if ("yes".equals(response)) {
                // Modify the signature if the user chooses "yes"
                digitalSignature = modifySignature(digitalSignature);
                System.out.println("Digital signature modified.");
            }

            Signature rsa = new Signature();
            boolean isValid = rsa.verify(publicKey, message, digitalSignature);
            System.out.println("Digital signature is valid: " + isValid);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    // method to modify the digital signature
    private static String modifySignature(String signature) {
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        signatureBytes[signatureBytes.length - 1] ^= 0xFF;
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
}