package view;

import controller.FilmController;
import controller.PlaylistController;
import model.Film;
import util.AuthService;
import util.FileManager;
import util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import javax.imageio.ImageIO;

/**
 * Panel home untuk user biasa yang menampilkan daftar film.
 * Menampilkan semua film yang visible beserta informasi detailnya dalam bentuk tabel.
 * User dapat melihat statistik jumlah film yang tersedia.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class HomePanel extends JPanel {
    private FilmController filmController;
    private PlaylistController playlistController;

    private JTable filmTable;
    private DefaultTableModel filmTableModel;
    private JLabel lblTotalFilms;

    /**
     * Konstruktor HomePanel.
     *
     * @param filmCtrl controller untuk mengelola data film
     * @param playlistCtrl controller untuk mengelola data playlist
     */
    public HomePanel(FilmController filmCtrl, PlaylistController playlistCtrl) {
        this.filmController = filmCtrl;
        this.playlistController = playlistCtrl;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFilmListPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        // Welcome message with login check
        String username = AuthService.isLoggedIn() ? AuthService.getCurrentUser().getUsername() : "Tamu";
        JLabel welcomeLabel = new JLabel("Selamat Datang, " + username + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        // Film Stats Card
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel filmCard = createStatsCard("Film Tersedia", "0", new Color(52, 152, 219));
        lblTotalFilms = (JLabel) ((JPanel) filmCard.getComponent(0)).getComponent(1);
        statsPanel.add(filmCard);

        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        contentPanel.setBackground(color);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);

        contentPanel.add(titleLabel);
        contentPanel.add(valueLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFilmListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("All Available Films"));

        String[] filmColumns = {"TMDB ID", "Title", "Director", "Genre", "Year", "Synopsis"};
        filmTableModel = new DefaultTableModel(filmColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        filmTable = new JTable(filmTableModel);
        filmTable.setRowHeight(25);
        JScrollPane filmScroll = new JScrollPane(filmTable);
        panel.add(filmScroll, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnViewDetails = new JButton("Lihat Detail");
        btnViewDetails.setBackground(new Color(52, 152, 219));
        btnViewDetails.setForeground(Color.BLACK);
        btnViewDetails.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnViewDetails.setFocusPainted(false);
        btnViewDetails.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewDetails.addActionListener(e -> showFilmDetails());

        JButton btnRefresh = new JButton("Muat Ulang");
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshData());

        actionPanel.add(btnViewDetails);
        actionPanel.add(btnRefresh);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Merefresh data tabel film dari database.
     * Memuat ulang semua film yang visible dan update statistik.
     * Menggunakan FileManager.fileExists() untuk verify data file sebelum load.
     */
    public void refreshData() {
        // Clear table
        filmTableModel.setRowCount(0);

        // Check if films data file exists before loading
        if (!FileManager.fileExists(FileManager.FILMS_FILE)) {
            ValidationUtil.showError(this, "File data film tidak ditemukan!");
            lblTotalFilms.setText("0");
            return;
        }

        // Populate film table (only visible films for users)
        int visibleCount = 0;
        for (Film film : filmController.getAllFilms()) {
            if (film.isVisible()) {
                filmTableModel.addRow(film.toTableRow());
                visibleCount++;
            }
        }

        // Update stats
        lblTotalFilms.setText(String.valueOf(visibleCount));
    }

    /**
     * Menampilkan dialog detail film yang dipilih dengan layout yang menarik.
     * Mirip dengan movie detail page dengan poster, info lengkap, dan synopsis.
     */
    private void showFilmDetails() {
        int selectedRow = filmTable.getSelectedRow();
        if (selectedRow < 0) {
            ValidationUtil.showError(this, "Pilih film terlebih dahulu untuk melihat detail!");
            return;
        }

        // Get selected film data from table
        String tmdbId = (String) filmTableModel.getValueAt(selectedRow, 0);
        String title = (String) filmTableModel.getValueAt(selectedRow, 1);
        String director = (String) filmTableModel.getValueAt(selectedRow, 2);
        String genre = (String) filmTableModel.getValueAt(selectedRow, 3);
        String year = filmTableModel.getValueAt(selectedRow, 4).toString();
        String synopsis = (String) filmTableModel.getValueAt(selectedRow, 5);

        // Get film object for poster path
        Film selectedFilm = null;
        for (Film f : filmController.getAllFilms()) {
            if (f.getId().equals(tmdbId)) {
                selectedFilm = f;
                break;
            }
        }

        // Create detail dialog
        JDialog detailDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
            "Detail Film", true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.setSize(800, 550);
        detailDialog.setLocationRelativeTo(this);

        // Header panel with title and year
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JPanel genrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        genrePanel.setOpaque(false);

        // Genre badge
        JLabel lblGenre = new JLabel(genre);
        lblGenre.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblGenre.setForeground(Color.WHITE);
        lblGenre.setBackground(new Color(231, 76, 60));
        lblGenre.setOpaque(true);
        lblGenre.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        genrePanel.add(lblGenre);

        JLabel lblYear = new JLabel("⭐ " + year);
        lblYear.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblYear.setForeground(new Color(241, 196, 15));
        genrePanel.add(lblYear);

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(genrePanel, BorderLayout.CENTER);

        // Content panel with film info
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Poster panel (left side)
        JPanel posterPanel = new JPanel(new BorderLayout());
        posterPanel.setBackground(Color.WHITE);
        posterPanel.setPreferredSize(new Dimension(200, 300));

        if (selectedFilm != null && selectedFilm.getPosterPath() != null && !selectedFilm.getPosterPath().isEmpty()) {
            try {
                String posterUrl = "https://image.tmdb.org/t/p/w500" + selectedFilm.getPosterPath();
                BufferedImage posterImage = ImageIO.read(URI.create(posterUrl).toURL());
                if (posterImage != null) {
                    Image scaledImage = posterImage.getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                    JLabel posterLabel = new JLabel(new ImageIcon(scaledImage));
                    posterLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
                    posterPanel.add(posterLabel, BorderLayout.CENTER);
                }
            } catch (Exception e) {
                JLabel noImageLabel = new JLabel("Poster tidak tersedia", SwingConstants.CENTER);
                noImageLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
                noImageLabel.setForeground(Color.GRAY);
                posterPanel.add(noImageLabel, BorderLayout.CENTER);
            }
        } else {
            JLabel noImageLabel = new JLabel("Poster tidak tersedia", SwingConstants.CENTER);
            noImageLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            noImageLabel.setForeground(Color.GRAY);
            posterPanel.add(noImageLabel, BorderLayout.CENTER);
        }

        // Right content panel with film details
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);

        // Info panel (director, etc)
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        infoPanel.setBackground(Color.WHITE);

        JLabel lblDirectorLabel = new JLabel("Sutradara:");
        lblDirectorLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblDirectorValue = new JLabel(director);
        lblDirectorValue.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel lblTmdbLabel = new JLabel("ID TMDB:");
        lblTmdbLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblTmdbValue = new JLabel(tmdbId);
        lblTmdbValue.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel directorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        directorPanel.setBackground(Color.WHITE);
        directorPanel.add(lblDirectorLabel);
        directorPanel.add(lblDirectorValue);

        JPanel tmdbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tmdbPanel.setBackground(Color.WHITE);
        tmdbPanel.add(lblTmdbLabel);
        tmdbPanel.add(lblTmdbValue);

        infoPanel.add(directorPanel);
        infoPanel.add(tmdbPanel);

        // Synopsis panel
        JPanel synopsisPanel = new JPanel(new BorderLayout(10, 10));
        synopsisPanel.setBackground(Color.WHITE);
        synopsisPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblSynopsisTitle = new JLabel("Sinopsis");
        lblSynopsisTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JTextArea txtSynopsis = new JTextArea(synopsis);
        txtSynopsis.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtSynopsis.setLineWrap(true);
        txtSynopsis.setWrapStyleWord(true);
        txtSynopsis.setEditable(false);
        txtSynopsis.setBackground(new Color(245, 245, 245));
        txtSynopsis.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane synopsisScroll = new JScrollPane(txtSynopsis);
        synopsisScroll.setBorder(null);

        synopsisPanel.add(lblSynopsisTitle, BorderLayout.NORTH);
        synopsisPanel.add(synopsisScroll, BorderLayout.CENTER);

        rightPanel.add(infoPanel, BorderLayout.NORTH);
        rightPanel.add(synopsisPanel, BorderLayout.CENTER);

        contentPanel.add(posterPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Tutup");
        btnClose.setBackground(new Color(52, 73, 94));
        btnClose.setForeground(Color.BLACK);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(btnClose);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add all panels to dialog
        detailDialog.add(headerPanel, BorderLayout.NORTH);
        detailDialog.add(contentPanel, BorderLayout.CENTER);

        detailDialog.setVisible(true);
    }
}
