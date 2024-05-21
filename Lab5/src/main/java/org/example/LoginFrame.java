package org.example;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    private static final String USERS_FILE = "users.txt";
    private static final Map<String, String> users = new HashMap<>();
    private static final Map<String, SecretKey> userKeys = new HashMap<>();

    // Load users from the file
    static {
        loadUsers();
    }

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        panel.add(loginButton);
        panel.add(registerButton);

        add(panel);

        loginButton.addActionListener(new LoginAction());
        registerButton.addActionListener(new RegisterAction());

        setVisible(true);
    }

    // Adds a new user with hashed password and generated key
    private static void addUser(String username, String password) {
        try {
            String hashedPassword = EncryptionUtils.hashPassword(password);
            users.put(username, hashedPassword);
            SecretKey key = EncryptionUtils.generateKeyFromPassword(password);
            userKeys.put(username, key);
            saveUsers();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    // Verifies the user's password
    private static boolean verifyUser(String username, String password) {
        String storedHash = users.get(username);
        if (storedHash == null) {
            return false;
        }
        try {
            return EncryptionUtils.verifyPassword(password, storedHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if the user exists
    private static boolean userExists(String username) {
        return users.containsKey(username);
    }

    // Retrieves the user's secret key
    private static SecretKey getUserSecretKey(String username) {
        return userKeys.get(username);
    }

    // Loads users from the file
    private static void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String username = parts[0];
                    String hashedPassword = parts[1];
                    String base64Key = parts[2];
                    SecretKey key = EncryptionUtils.decodeKey(base64Key);
                    users.put(username, hashedPassword);
                    userKeys.put(username, key);
                }
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    // Saves users to the file
    private static void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String username = entry.getKey();
                String hashedPassword = entry.getValue();
                SecretKey key = userKeys.get(username);
                String base64Key = EncryptionUtils.encodeKey(key);
                bw.write(username + "," + hashedPassword + "," + base64Key);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handles the login action
    class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();

            if (verifyUser(username, new String(password))) {
                try {
                    SecretKey key = getUserSecretKey(username);
                    PasswordStorage passwordStorage = new PasswordStorage(key, username);
                    passwordStorage.decryptFile(); // Decrypt the file on login
                    new PasswordManagerFrame(passwordStorage, username, key);
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid login credentials!");
            }
        }
    }

    // Handles the register action
    class RegisterAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            char[] password = passwordField.getPassword();

            if (userExists(username)) {
                JOptionPane.showMessageDialog(null, "User already exists!");
            } else {
                addUser(username, new String(password));
                JOptionPane.showMessageDialog(null, "User registered successfully!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
