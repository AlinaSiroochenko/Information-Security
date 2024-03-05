package org.example;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Select the operation: \n1 - Encrypt\n2 - Decrypt from file");
            int operationChoice = scanner.nextInt();
            scanner.nextLine();

            switch (operationChoice) {
                case 1:
                    // Encryption
                    handleEncryption(scanner);
                    break;
                case 2:
                    // Decryption
                    handleDecryption();
                    break;
                default:
                    System.out.println("Incorrect operation selection");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey Key(String keyInput) {
        if (keyInput.isEmpty()) {
            // Convert the entered string to a binary byte array
            byte[] keyBytes = Base64.getDecoder().decode(keyInput);
            return CryptoUtils.parseKey(keyBytes);
        }
        return null;
    }

    private static void handleEncryption(Scanner scanner) {
        try {
            System.out.println("Enter the text to encrypt:");
            String inputText = scanner.nextLine();

            System.out.println("Enter the secret key:");
            String keyInput = scanner.nextLine();

            System.out.println("Select the block cipher mode\n1 - ECB\n2 - CBC\n3 - CFB");
            int blockCipherModeChoice = scanner.nextInt();
            scanner.nextLine();

            String blockCipherMode = getBlockCipherMode(blockCipherModeChoice);

            IvParameterSpec iv = null;
            if ("CBC".equals(blockCipherMode) || "CFB".equals(blockCipherMode)) {
                // Requesting the user's initialization vector (IV) for CBC and CFB modes
                System.out.println("Enter the initialization vector (IV):");
                String ivInput = scanner.nextLine();
                byte[] ivBytes = ivInput.getBytes(StandardCharsets.UTF_8);
                iv = new IvParameterSpec(ivBytes);
            }

            // Checking and supplementing the key, if necessary
            byte[] keyBytes = Arrays.copyOf(keyInput.getBytes(StandardCharsets.UTF_8), 16);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            // Encrypted text
            String encryptedText = CryptoUtils.encrypt("AES", blockCipherMode, inputText, secretKey, iv);
            System.out.println("Encrypted text: " + encryptedText);

            // Write the encrypted text to a file
            saveToFile(encryptedText, "encrypted_text.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleDecryption() {
        try {
            // Read encrypted text from file
            String encryptedText = readFromFile("encrypted_text.txt");

            if (encryptedText == null || encryptedText.isEmpty()) {
                System.out.println("No encrypted text found in the file.");
                return;
            }

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter the secret key:");
            String keyInput = scanner.nextLine();

            System.out.println("Select the block cipher mode\n1 - ECB\n2 - CBC\n3 - CFB");
            int blockCipherModeChoice = scanner.nextInt();
            scanner.nextLine();

            String blockCipherMode = getBlockCipherMode(blockCipherModeChoice);

            IvParameterSpec iv = null;
            if ("CBC".equals(blockCipherMode) || "CFB".equals(blockCipherMode)) {
                System.out.println("Enter the initialization vector (IV):");
                String ivInput = scanner.nextLine();

                // Checking the length of the entered IV
                if (ivInput.length() != 16) {
                    System.out.println("The length of IV. Enter 16 bytes.");
                    return;
                }

                byte[] ivBytes = ivInput.getBytes(StandardCharsets.UTF_8);
                iv = new IvParameterSpec(ivBytes);
            }

            // Checking and supplementing the key, if necessary
            byte[] keyBytes = Arrays.copyOf(keyInput.getBytes(StandardCharsets.UTF_8), 16);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            // For ECB mode, we use null as the initialization vector
            String decryptedText = CryptoUtils.decrypt("AES", blockCipherMode, encryptedText, secretKey, iv);
            System.out.println("Decrypted text: " + decryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile(String content, String filename) {
        try {
            Files.write(Paths.get(filename), content.getBytes());
            System.out.println("The result is saved in the file " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getBlockCipherMode(int choice) {
        switch (choice) {
            case 1:
                return "ECB";
            case 2:
                return "CBC";
            case 3:
                return "CFB";
            default:
                throw new IllegalArgumentException("Incorrect selection of the block cipher mode.");
        }
    }
}