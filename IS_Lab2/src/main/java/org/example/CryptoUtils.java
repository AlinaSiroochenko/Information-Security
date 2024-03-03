package org.example;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

    public class CryptoUtils {

        public static SecretKey generateRandomKey() throws Exception {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        }

        public static String encrypt(String algorithm, String mode, String input, SecretKey key, IvParameterSpec iv)
                throws Exception {
            Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/PKCS5Padding");
            if (iv != null) {
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            byte[] encryptedBytes = cipher.doFinal(input.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }

        public static String decrypt(String algorithm, String mode, String input, SecretKey key, IvParameterSpec iv)
                throws Exception {
            Cipher cipher = Cipher.getInstance(algorithm + "/" + mode + "/PKCS5Padding");
            if (iv != null) {
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            byte[] decodedInput = Base64.getDecoder().decode(input);
            byte[] decryptedBytes = cipher.doFinal(decodedInput);
            return new String(decryptedBytes);
        }

        public static SecretKey parseKey(byte[] keyBytes) {
            return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
        }
    }
