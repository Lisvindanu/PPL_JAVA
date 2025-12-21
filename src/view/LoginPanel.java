package view;

import util.AuthService;
import util.ValidationUtil;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Panel untuk tampilan login dan registrasi pengguna.
 * Menyediakan form login dengan email dan password,
 * serta tombol untuk membuka dialog registrasi user baru.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class LoginPanel extends JPanel {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private Runnable onLoginSuccess;

    /**
     * Konstruktor LoginPanel.
     *
     * @param onLoginSuccess callback yang dipanggil ketika login berhasil
     */
    public LoginPanel(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        setLayout(new GridBagLayout());
        setBackground(new Color(45, 52, 54));

        createLoginForm();
    }

    /**
     * Membuat komponen form login dengan layout dan styling.
     * Berisi field email, password, dan tombol login/register.
     */
    private void createLoginForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Film Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        // Login form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(8, 8, 8, 8);
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        formGbc.gridx = 0; formGbc.gridy = 0; formGbc.gridwidth = 2;
        JLabel lblEmail = new JLabel("Email:");
        formPanel.add(lblEmail, formGbc);

        formGbc.gridy = 1;
        txtEmail = new JTextField(25);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtEmail, formGbc);

        // Password
        formGbc.gridy = 2;
        JLabel lblPassword = new JLabel("Password:");
        formPanel.add(lblPassword, formGbc);

        formGbc.gridy = 3;
        txtPassword = new JPasswordField(25);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtPassword, formGbc);

        // Buttons
        formGbc.gridy = 4; formGbc.gridwidth = 1;
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.addActionListener(e -> handleLogin());
        formPanel.add(btnLogin, formGbc);

        formGbc.gridx = 1;
        btnRegister = new JButton("Register");
        btnRegister.setBackground(new Color(46, 204, 113));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setFocusPainted(false);
        btnRegister.setOpaque(true);
        btnRegister.setBorderPainted(false);
        btnRegister.addActionListener(e -> showRegisterDialog());
        formPanel.add(btnRegister, formGbc);

        gbc.gridy = 2; gbc.gridwidth = 2;
        add(formPanel, gbc);

        // Enter key support
        txtPassword.addActionListener(e -> handleLogin());
    }

    /**
     * Menangani proses login ketika tombol login diklik.
     * Memvalidasi input dan memanggil AuthService untuk autentikasi.
     */
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            ValidationUtil.showError(this, "Please fill all fields!");
            return;
        }

        User user = AuthService.login(email, password);
        if (user != null) {
            ValidationUtil.showSuccess(this, "Welcome, " + user.getUsername() + "!");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            ValidationUtil.showError(this, "Invalid email or password!");
        }
    }

    /**
     * Menampilkan dialog untuk registrasi user baru.
     * Meminta input email, username, password, dan konfirmasi password.
     */
    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Register", true);
        registerDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtRegEmail = new JTextField(20);
        JTextField txtRegUsername = new JTextField(20);
        JPasswordField txtRegPassword = new JPasswordField(20);
        JPasswordField txtConfirmPassword = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        registerDialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(txtRegEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        registerDialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(txtRegUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        registerDialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(txtRegPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        registerDialog.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        registerDialog.add(txtConfirmPassword, gbc);

        JButton btnSubmit = new JButton("Register");
        btnSubmit.addActionListener(e -> {
            String email = txtRegEmail.getText().trim();
            String username = txtRegUsername.getText().trim();
            String password = new String(txtRegPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(registerDialog, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (AuthService.register(email, password, username)) {
                JOptionPane.showMessageDialog(registerDialog, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        registerDialog.add(btnSubmit, gbc);

        registerDialog.pack();
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setVisible(true);
    }
}
