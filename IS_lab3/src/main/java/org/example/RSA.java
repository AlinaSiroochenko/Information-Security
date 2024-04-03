package org.example;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RSA {

    // Method to find the greatest common divisor
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        return a;
    }

    // Method for extended Euclidean algorithm
    public static BigInteger[] extendedGCD(BigInteger a, BigInteger b) {
        if (a.equals(BigInteger.ZERO)) {
            return new BigInteger[]{b, BigInteger.ZERO, BigInteger.ONE};
        } else {
            BigInteger[] vals = extendedGCD(b.mod(a), a);
            BigInteger d = vals[0];
            BigInteger x = vals[2].subtract(b.divide(a).multiply(vals[1]));
            return new BigInteger[]{d, x, vals[1]};
        }
    }

    // Method to compute the modular inverse of a number
    public static BigInteger modInverse(BigInteger a, BigInteger m) {
        BigInteger[] vals = extendedGCD(a, m);
        if (!vals[0].equals(BigInteger.ONE)) {
            throw new ArithmeticException("Modular inverse does not exist");
        } else {
            BigInteger result = vals[1].mod(m);
            if (result.compareTo(BigInteger.ZERO) < 0) {
                result = result.add(m);
            }
            return result;
        }
    }

    // Method to generate an RSA key pair
    public static List<BigInteger> generateKeyPair(BigInteger p, BigInteger q) {
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.valueOf(65537); // Set the public exponent value
        BigInteger d = modInverse(e, phi);
        return List.of(n, e, d);
    }

    // Method to encrypt text using a key
    public static List<BigInteger> encrypt(String plaintext, BigInteger n, BigInteger e) {
        byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
        List<BigInteger> cipher = new ArrayList<>();
        for (byte b : bytes) {
            BigInteger charValue = BigInteger.valueOf(b);
            cipher.add(charValue.modPow(e, n));
        }
        return cipher;
    }

    // Method to save keys to a file
    public static void saveKeys(String filename, BigInteger n, BigInteger e, BigInteger d) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(n);
            writer.println(e);
            writer.println(d);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Method to save encrypted text to a file
    public static void saveEncryptedText(String filename, List<BigInteger> encryptedText) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (BigInteger encryptedChar : encryptedText) {
                writer.println(encryptedChar);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    // Method to read keys from a file
    public static List<BigInteger> readKeys(String filename) {
        List<BigInteger> keys = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            BigInteger n = scanner.nextBigInteger();
            BigInteger e = scanner.nextBigInteger();
            BigInteger d = scanner.nextBigInteger();
            keys.add(n);
            keys.add(e);
            keys.add(d);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return keys;
    }

    // Method to read encrypted text from a file
    public static List<BigInteger> readEncryptedText(String filename) {
        List<BigInteger> ciphertext = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextBigInteger()) {
                ciphertext.add(scanner.nextBigInteger());
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return ciphertext;
    }

    // Method to decrypt text using a key
    public static String decrypt(List<BigInteger> ciphertext, BigInteger n, BigInteger d) {
        StringBuilder plainText = new StringBuilder();
        for (BigInteger encryptedChar : ciphertext) {
            BigInteger decryptedChar = encryptedChar.modPow(d, n);
            plainText.append((char) decryptedChar.intValue());
        }
        return plainText.toString();
    }

}
