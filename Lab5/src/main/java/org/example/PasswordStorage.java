package org.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class PasswordStorage {
    private String fileName;
    private String encryptedFileName;
    private List<PasswordEntry> passwords;
    private SecretKey secretKey;

    public PasswordStorage(SecretKey key, String username) {
        this.secretKey = key;
        this.fileName = username + "_passwords.txt";
        this.encryptedFileName = username + "_passwords.enc";
        passwords = new ArrayList<>();
        try {
            if (new File(encryptedFileName).exists()) {
                decryptFile();
            } else {
                savePasswords();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adds a new password entry
    public void addPassword(PasswordEntry entry) {
        passwords.add(entry);
        savePasswords();
    }

    // Searches for password entries by title
    public List<PasswordEntry> searchPassword(String title) {
        List<PasswordEntry> result = new ArrayList<>();
        for (PasswordEntry entry : passwords) {
            if (entry.getTitle().equalsIgnoreCase(title)) {
                result.add(entry);
            }
        }
        return result;
    }

    // Updates an existing password by title
    public void updatePassword(String title, String newPassword) {
        for (PasswordEntry entry : passwords) {
            if (entry.getTitle().equalsIgnoreCase(title)) {
                entry.setPassword(newPassword);
                break;
            }
        }
        savePasswords();
    }

    // Deletes a password entry by title
    public void deletePassword(String title) {
        passwords.removeIf(entry -> entry.getTitle().equalsIgnoreCase(title));
        savePasswords();
    }

    // Returns all password entries
    public List<PasswordEntry> getAllPasswords() {
        return passwords;
    }

    // Loads passwords from the file
    private void loadPasswords() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String title = parts[0];
                    String encryptedPassword = parts[1];
                    String url = parts[2];
                    String info = parts[3];
                    String password = EncryptionUtils.decrypt(encryptedPassword, secretKey);
                    passwords.add(new PasswordEntry(title, password, url, info));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Saves passwords to the file and encrypts it
    private void savePasswords() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (PasswordEntry entry : passwords) {
                String encryptedPassword = EncryptionUtils.encrypt(entry.getPassword(), secretKey);
                bw.write(entry.getTitle() + "," + encryptedPassword + "," + entry.getUrl() + "," + entry.getInfo());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            encryptFile(); // Encrypt the file after saving
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Encrypts the password file
    public void encryptFile() throws GeneralSecurityException, IOException {
        try (FileInputStream fis = new FileInputStream(fileName);
             FileOutputStream fos = new FileOutputStream(encryptedFileName)) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] input = new byte[64];
            int bytesRead;

            while ((bytesRead = fis.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null) {
                    fos.write(output);
                }
            }

            byte[] output = cipher.doFinal();
            if (output != null) {
                fos.write(output);
            }
        }
        new File(fileName).delete(); // Delete the plaintext file after encryption
    }

    // Decrypts the password file
    public void decryptFile() throws GeneralSecurityException, IOException {
        try (FileInputStream fis = new FileInputStream(encryptedFileName);
             FileOutputStream fos = new FileOutputStream(fileName)) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] input = new byte[64];
            int bytesRead;

            while ((bytesRead = fis.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null) {
                    fos.write(output);
                }
            }

            byte[] output = cipher.doFinal();
            if (output != null) {
                fos.write(output);
            }
        }
        loadPasswords(); // Load the passwords after decryption
    }

    // Decrypts a single password
    public String decryptPassword(String encryptedPassword) throws Exception {
        return EncryptionUtils.decrypt(encryptedPassword, secretKey);
    }
}
