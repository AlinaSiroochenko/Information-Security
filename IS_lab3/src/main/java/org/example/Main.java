package org.example;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose operation:");
        System.out.println("1. Encrypt");
        System.out.println("2. Decrypt");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 1) {
            System.out.print("Enter plaintext: ");
            String plaintext = scanner.nextLine();
            System.out.print("Enter p: ");
            BigInteger p = scanner.nextBigInteger();
            System.out.print("Enter q: ");
            BigInteger q = scanner.nextBigInteger();
            List<BigInteger> keyPair = RSA.generateKeyPair(p, q);
            BigInteger nEncrypt = keyPair.get(0);
            BigInteger eEncrypt = keyPair.get(1);
            BigInteger dEncrypt = keyPair.get(2);
            List<BigInteger> encryptedText = RSA.encrypt(plaintext, nEncrypt, eEncrypt);
            RSA.saveKeys("keys.txt", nEncrypt, eEncrypt, dEncrypt);
            RSA.saveEncryptedText("encrypted_text.txt", encryptedText);
            System.out.println("Encrypted text saved to encrypted_text.txt");
            System.out.println("Public and private keys saved to keys.txt");
        } else if (choice == 2) {
            List<BigInteger> keys = RSA.readKeys("keys.txt");
            BigInteger nDecrypt = keys.get(0);
            BigInteger eDecrypt = keys.get(1);
            BigInteger dDecrypt = keys.get(2);
            List<BigInteger> ciphertext = RSA.readEncryptedText("encrypted_text.txt");
            String decryptedText = RSA.decrypt(ciphertext, nDecrypt, dDecrypt);
            System.out.println("Decrypted text: " + decryptedText);
        } else {
            System.out.println("Invalid choice. Please enter either 1 or 2.");
        }
    }
}
