package view;

import controller.FilmController;
import model.User;
import util.AuthService;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel admin untuk mengelola film dan user.
 * Menggunakan tabbed pane untuk memisahkan manajemen film dan manajemen user.
 * Admin dapat menambah/hapus film dan mengubah status premium user.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class AdminPanel extends JPanel {
    private FilmController filmController;

    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JPanel filmPanel;

    /**
     * Konstruktor AdminPanel.
     *
     * @param filmCtrl controller untuk mengelola data film
     */
    public AdminPanel(FilmController filmCtrl) {
        this.filmController = filmCtrl;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabbed pane for admin functions
        JTabbedPane tabbedPane = new JTabbedPane();

        // Film Management Tab
        filmPanel = new FilmPanel(filmController);
        tabbedPane.addTab("Manage Films", filmPanel);

        // User Management Tab
        tabbedPane.addTab("Manage Users", createUserManagementPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // User table
        String[] columns = {"Email", "Username", "Role", "Account Type"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnTogglePremium = new JButton("Toggle Premium Status");
        JButton btnRefresh = new JButton("Refresh");

        btnTogglePremium.setBackground(new Color(52, 152, 219));
        btnTogglePremium.setForeground(Color.WHITE);
        btnTogglePremium.setFocusPainted(false);
        btnTogglePremium.setOpaque(true);
        btnTogglePremium.setBorderPainted(false);
        btnTogglePremium.addActionListener(e -> togglePremiumStatus());

        btnRefresh.addActionListener(e -> refreshUsers());

        actionPanel.add(btnTogglePremium);
        actionPanel.add(btnRefresh);
        panel.add(actionPanel, BorderLayout.SOUTH);

        refreshUsers();

        return panel;
    }

    private void togglePremiumStatus() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            ValidationUtil.showError(this, "Please select a user first!");
            return;
        }

        String email = (String) userTableModel.getValueAt(selectedRow, 0);
        List<User> allUsers = AuthService.getAllUsers();

        for (User user : allUsers) {
            if (user.getEmail().equals(email)) {
                if (user.isAdmin()) {
                    ValidationUtil.showError(this, "Cannot modify admin account!");
                    return;
                }

                user.setPremium(!user.isPremium());
                AuthService.updateUser(user);
                refreshUsers();

                String status = user.isPremium() ? "Premium" : "Free";
                ValidationUtil.showSuccess(this, "User account updated to: " + status);
                return;
            }
        }
    }

    private void refreshUsers() {
        userTableModel.setRowCount(0);
        List<User> allUsers = AuthService.getAllUsers();

        for (User user : allUsers) {
            userTableModel.addRow(user.toTableRow());
        }
    }
}
