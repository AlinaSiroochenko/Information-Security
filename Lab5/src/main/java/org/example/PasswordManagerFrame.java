package org.example;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.util.List;

public class PasswordManagerFrame extends JFrame {
    private JTable passwordTable;
    private JButton addButton, updateButton, deleteButton, searchButton, showButton, copyButton, generateButton;
    private PasswordStorage passwordStorage;
    private SecretKey secretKey;
    private String username;
    private boolean showPassword = false;

    // Constructor to initialize the PasswordManagerFrame
    public PasswordManagerFrame(PasswordStorage passwordStorage, String username, SecretKey secretKey) {
        this.passwordStorage = passwordStorage;
        this.username = username;
        this.secretKey = secretKey;

        setTitle("Password Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    passwordStorage.encryptFile(); // Encrypt file on program close
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        passwordTable = new JTable();
        updateTable();

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        searchButton = new JButton("Search");
        showButton = new JButton("Show");
        copyButton = new JButton("Copy");
        generateButton = new JButton("Generate Password");

        panel.add(new JScrollPane(passwordTable));
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(searchButton);
        panel.add(showButton);
        panel.add(copyButton);
        panel.add(generateButton);

        add(panel);

        addButton.addActionListener(new AddAction());
        updateButton.addActionListener(new UpdateAction());
        deleteButton.addActionListener(new DeleteAction());
        searchButton.addActionListener(new SearchAction());
        showButton.addActionListener(new ShowAction());
        copyButton.addActionListener(new CopyAction());
        generateButton.addActionListener(new GenerateAction());

        setVisible(true);
    }

    // Updates the table with the latest password data
    private void updateTable() {
        List<PasswordEntry> passwords = passwordStorage.getAllPasswords();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Title", "Password", "URL", "Info"}, 0);
        for (PasswordEntry entry : passwords) {
            String displayedPassword = showPassword ? entry.getPassword() : "********";
            model.addRow(new Object[]{entry.getTitle(), displayedPassword, entry.getUrl(), entry.getInfo()});
        }
        passwordTable.setModel(model);
        TableColumnModel columnModel = passwordTable.getColumnModel();
        columnModel.getColumn(1).setPreferredWidth(200);  // Column for passwords
    }

    // Action listener for adding a new password entry
    class AddAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String title = JOptionPane.showInputDialog("Enter title:");
            String password = JOptionPane.showInputDialog("Enter password:");
            String url = JOptionPane.showInputDialog("Enter URL:");
            String info = JOptionPane.showInputDialog("Enter additional info:");

            if (title == null || title.trim().isEmpty() ||
                    password == null || password.trim().isEmpty() ||
                    url == null || url.trim().isEmpty() ||
                    info == null || info.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PasswordEntry entry = new PasswordEntry(title, password, url, info);
            passwordStorage.addPassword(entry);
            updateTable();
        }
    }

    // Action listener for updating an existing password entry
    class UpdateAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String title = JOptionPane.showInputDialog("Enter title to update:");
            if (title == null || title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newPassword = JOptionPane.showInputDialog("Enter new password:");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            passwordStorage.updatePassword(title, newPassword);
            updateTable();
        }
    }

    // Action listener for deleting an existing password entry
    class DeleteAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String title = JOptionPane.showInputDialog("Enter title to delete:");
            if (title == null || title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            passwordStorage.deletePassword(title);
            updateTable();
        }
    }

    // Action listener for searching password entries by title
    class SearchAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String title = JOptionPane.showInputDialog("Enter title to search:");
            if (title == null || title.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Title cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<PasswordEntry> results = passwordStorage.searchPassword(title);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No entries found!");
            } else {
                DefaultTableModel model = new DefaultTableModel(new Object[]{"Title", "Password", "URL", "Info"}, 0);
                for (PasswordEntry entry : results) {
                    String displayedPassword = showPassword ? entry.getPassword() : "********";
                    model.addRow(new Object[]{entry.getTitle(), displayedPassword, entry.getUrl(), entry.getInfo()});
                }
                passwordTable.setModel(model);
            }
        }
    }

    // Action listener for toggling password visibility
    class ShowAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showPassword = !showPassword;
            updateTable();
        }
    }

    // Action listener for copying a password to the clipboard
    class CopyAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = passwordTable.getSelectedRow();
            if (selectedRow >= 0) {
                String password = (String) passwordTable.getValueAt(selectedRow, 1);
                if (!showPassword) {
                    try {
                        PasswordEntry entry = passwordStorage.getAllPasswords().get(selectedRow);
                        password = entry.getPassword();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error decrypting password!");
                    }
                }
                StringSelection stringSelection = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(null, "Password copied to clipboard!");
            } else {
                JOptionPane.showMessageDialog(null, "No entry selected!");
            }
        }
    }

    // Action listener for generating a random password
    class GenerateAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String generatedPassword = generateRandomPassword(12); // Generate a password of length 12
            JOptionPane.showMessageDialog(null, "Generated Password: " + generatedPassword);
        }

        // Generates a random password of given length
        private String generateRandomPassword(int length) {
            final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";
            SecureRandom random = new SecureRandom();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(chars.length());
                sb.append(chars.charAt(randomIndex));
            }
            return sb.toString();
        }
    }
}
