package main;

import view.*;
import controller.FilmController;
import controller.PlaylistController;
import util.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Kelas aplikasi utama untuk Sistem Manajemen Film.
 * Kelas ini merupakan entry point dan controller utama untuk seluruh aplikasi.
 * Mengelola navigasi antar panel berdasarkan autentikasi dan role pengguna (Admin/User).
 *
 * @author lisvindanu
 * @version 3.0
 */
public class MainApp extends JFrame {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MainApp.class);
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Shared controllers
    private FilmController filmController;
    private PlaylistController playlistController;

    // Panels
    private LoginPanel loginPanel;
    private AdminPanel adminPanel;
    private MyPlaylistPanel myPlaylistPanel;
    private HomePanel homePanel;

    /**
     * Konstruktor untuk membuat window aplikasi utama.
     * Menginisialisasi properti frame, controller, dan CardLayout untuk perpindahan panel.
     * Secara default menampilkan panel login.
     */
    public MainApp() {
        setTitle("Film Management System");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize controllers
        filmController = new FilmController();
        playlistController = new PlaylistController();

        // Create CardLayout for switching panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize login panel
        loginPanel = new LoginPanel(this::onLoginSuccess);
        contentPanel.add(loginPanel, "login");

        // Add content panel
        add(contentPanel);

        // Show login panel
        cardLayout.show(contentPanel, "login");
    }

    /**
     * Callback yang dipanggil ketika login berhasil.
     * Menginisialisasi panel yang sesuai berdasarkan role pengguna (Admin atau User).
     */
    private void onLoginSuccess() {
        // Initialize panels based on user role
        if (AuthService.isAdmin()) {
            initializeAdminUI();
        } else {
            initializeUserUI();
        }
    }

    /**
     * Menginisialisasi tampilan UI untuk pengguna dengan role Admin.
     * Menampilkan panel admin yang berisi manajemen film dan manajemen user.
     */
    private void initializeAdminUI() {
        // Remove login panel
        contentPanel.removeAll();

        // Admin gets: Admin Panel (Films + User Management)
        adminPanel = new AdminPanel(filmController);
        contentPanel.add(adminPanel, "admin");

        // Create menu bar
        createAdminMenuBar();

        // Show admin panel
        cardLayout.show(contentPanel, "admin");
        revalidate();
        repaint();
    }

    /**
     * Menginisialisasi tampilan UI untuk pengguna dengan role User.
     * Menampilkan panel home (daftar film) dan panel playlist milik user.
     */
    private void initializeUserUI() {
        // Remove login panel
        contentPanel.removeAll();

        // Regular user gets: Home (all films) + My Playlists
        homePanel = new HomePanel(filmController, playlistController);
        myPlaylistPanel = new MyPlaylistPanel(playlistController, filmController);

        contentPanel.add(homePanel, "home");
        contentPanel.add(myPlaylistPanel, "myplaylists");

        // Create menu bar
        createUserMenuBar();

        // Show home panel and refresh data
        homePanel.refreshData();
        cardLayout.show(contentPanel, "home");
        revalidate();
        repaint();
    }

    /**
     * Membuat menu bar untuk pengguna Admin.
     * Berisi menu File (Logout) dan Help (About) dengan keyboard shortcuts.
     */
    private void createAdminMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        logoutItem.addActionListener(e -> logout());
        fileMenu.add(logoutItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Membuat menu bar untuk pengguna User.
     * Berisi menu File (Home, My Playlists, Logout) dan Help (About) dengan keyboard shortcuts.
     */
    private void createUserMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem homeItem = new JMenuItem("Home");
        homeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
        homeItem.addActionListener(e -> {
            homePanel.refreshData();
            cardLayout.show(contentPanel, "home");
        });

        JMenuItem myPlaylistsItem = new JMenuItem("My Playlists");
        myPlaylistsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        myPlaylistsItem.addActionListener(e -> cardLayout.show(contentPanel, "myplaylists"));

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        logoutItem.addActionListener(e -> logout());

        fileMenu.add(homeItem);
        fileMenu.add(myPlaylistsItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Menangani proses logout pengguna.
     * Menampilkan konfirmasi menggunakan ValidationUtil, membersihkan session, dan kembali ke panel login.
     */
    private void logout() {
        if (util.ValidationUtil.confirmAction(this, "Apakah Anda yakin ingin logout?")) {
            AuthService.logout();

            // Clear everything and show login again
            contentPanel.removeAll();
            loginPanel = new LoginPanel(this::onLoginSuccess);
            contentPanel.add(loginPanel, "login");
            setJMenuBar(null);
            cardLayout.show(contentPanel, "login");
            revalidate();
            repaint();
        }
    }

    /**
     * Menampilkan dialog informasi tentang aplikasi.
     * Berisi versi aplikasi, informasi user yang login, dan daftar fitur.
     */
    private void showAbout() {
        String userInfo = AuthService.isAdmin() ? "Admin" : "User";
        String welcomeMsg = "Welcome, " + AuthService.getCurrentUser().getUsername() + " (" + userInfo + ")";

        JOptionPane.showMessageDialog(this,
                "<html><h2>Film Management System</h2>" +
                "<p>Version: 2.0 (Auth Edition)</p>" +
                "<p>" + welcomeMsg + "</p>" +
                "<p>Features:</p>" +
                "<ul>" +
                "<li>User Authentication (Login/Register)</li>" +
                "<li>Role-based Access (Admin/User)</li>" +
                "<li>Film Management with TMDB API</li>" +
                "<li>Playlist Management</li>" +
                "<li>Persistent Data Storage</li>" +
                "</ul></html>",
                "About Film Management System",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Method main sebagai entry point aplikasi.
     * Mengatur Look and Feel sistem dan menjalankan aplikasi pada Event Dispatch Thread.
     *
     * @param args argumen command line (tidak digunakan)
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Failed to initialize application look and feel", e);
        }

        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}