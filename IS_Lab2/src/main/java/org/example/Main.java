package org.example;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Select the mode (1 - Encryption, 2 - Decryption):");
            int modeChoice = scanner.nextInt();
            scanner.nextLine(); // Clearing the buffer after entering a number

            switch (modeChoice) {
                case 1:
                    // Encryption
                    handleEncryption(scanner);
                    break;
                case 2:
                    // Decryption
                    handleDecryption(scanner);
                    break;
                default:
                    System.out.println("Incorrect mode selection");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKey generateOrParseKey(String keyInput) {
        if (keyInput.isEmpty()) {
            // Generate a random secret key
            try {
                return CryptoUtils.generateRandomKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
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

            System.out.println("Select the block cipher mode (1 - ECB, 2 - CBC, 3 - CFB):");
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

    private static IvParameterSpec generateRandomIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static void handleDecryption(Scanner scanner) {
        try {
            System.out.println("Enter the encrypted text:");
            String encryptedText = scanner.nextLine();

            System.out.println("Enter the secret key:");
            String keyInput = scanner.nextLine();

            System.out.println("Select the block cipher mode (1 - ECB, 2 - CBC, 3 - CFB):");
            int blockCipherModeChoice = scanner.nextInt();
            scanner.nextLine(); // Clearing the buffer after entering a number

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
