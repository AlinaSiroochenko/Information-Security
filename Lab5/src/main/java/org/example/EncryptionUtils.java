package org.example;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.SecureRandom;
import java.util.Base64;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class EncryptionUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";

     //Encrypts the given data using AES.
    public static String encrypt(String data, SecretKey key) throws Exception {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Data to encrypt cannot be null or empty");
        }
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

     //Decrypts the given data using AES.
    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        if (encryptedData == null || encryptedData.trim().isEmpty()) {
            throw new IllegalArgumentException("Data to decrypt cannot be null or empty");
        }
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, "BC");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }


     //Generates a new AES SecretKey.
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM, "BC");
        keyGen.init(256, new SecureRandom());
        return keyGen.generateKey();
    }

     //Generates a SecretKey from a password using PBKDF2.
    public static SecretKey generateKeyFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

     //Hashes a password using PBKDF2.
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }


     //Verifies a password against a stored hash.
    public static boolean verifyPassword(String password, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedHash.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] hash = Base64.getDecoder().decode(parts[1]);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        byte[] testHash = factory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(testHash).equals(Base64.getEncoder().encodeToString(hash));
    }


     //Encodes a SecretKey to a Base64 string.
    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    //Decodes a SecretKey from a Base64 string.
    public static SecretKey decodeKey(String base64Key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }
}
