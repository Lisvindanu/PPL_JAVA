package view;

import controller.FilmController;
import controller.PlaylistController;
import model.Film;
import model.Playlist;
import util.AuthService;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel untuk mengelola playlist milik user yang sedang login.
 * User dapat membuat playlist baru, menambah/hapus film dari playlist,
 * dan menghapus playlist. Hanya menampilkan playlist milik user sendiri.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class MyPlaylistPanel extends JPanel {
    private PlaylistController playlistController;
    private FilmController filmController;

    private JTable playlistTable;
    private DefaultTableModel playlistTableModel;
    private JList<String> filmList;
    private DefaultListModel<String> filmListModel;

    /**
     * Konstruktor MyPlaylistPanel.
     * Menginisialisasi controllers dan membuat UI dengan header dan content panel.
     *
     * @param playlistCtrl controller untuk mengelola data playlist
     * @param filmCtrl controller untuk mengelola data film
     */
    public MyPlaylistPanel(PlaylistController playlistCtrl, FilmController filmCtrl) {
        this.playlistController = playlistCtrl;
        this.filmController = filmCtrl;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    /**
     * Membuat panel header yang berisi judul dan tombol create playlist.
     *
     * @return JPanel header dengan title dan tombol create
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("My Playlists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnCreatePlaylist = new JButton("Create New Playlist");
        btnCreatePlaylist.setBackground(new Color(46, 204, 113));
        btnCreatePlaylist.setForeground(Color.WHITE);
        btnCreatePlaylist.setFocusPainted(false);
        btnCreatePlaylist.setOpaque(true);
        btnCreatePlaylist.setBorderPainted(false);
        btnCreatePlaylist.addActionListener(e -> createNewPlaylist());

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(btnCreatePlaylist, BorderLayout.EAST);

        return panel;
    }

    /**
     * Membuat panel konten utama yang berisi tabel playlist dan action buttons.
     * Tabel menampilkan nama playlist, jumlah film, dan daftar film.
     *
     * @return JPanel konten dengan tabel dan tombol aksi
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Playlist table
        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.setBorder(BorderFactory.createTitledBorder("Your Playlists"));

        String[] columns = {"Playlist Name", "Film Count", "Films"};
        playlistTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        playlistTable = new JTable(playlistTableModel);
        playlistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(playlistTable);
        playlistPanel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons with styling
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnViewFilms = new JButton("Lihat Detail Film");
        JButton btnAddFilms = new JButton("Add Films to Selected Playlist");
        JButton btnDelete = new JButton("Delete Playlist");
        JButton btnRefresh = new JButton("Refresh");

        // Style View Films button (Green)
        btnViewFilms.setBackground(new Color(46, 204, 113));
        btnViewFilms.setForeground(Color.BLACK);
        btnViewFilms.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnViewFilms.setFocusPainted(false);
        btnViewFilms.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewFilms.addActionListener(e -> viewPlaylistFilms());

        // Style Add Films button (Blue)
        btnAddFilms.setBackground(new Color(52, 152, 219));
        btnAddFilms.setForeground(Color.BLACK);
        btnAddFilms.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAddFilms.setFocusPainted(false);
        btnAddFilms.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddFilms.addActionListener(e -> addFilmsToPlaylist());

        // Style Delete button (Red)
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deletePlaylist());

        // Style Refresh button (Gray)
        btnRefresh.setBackground(new Color(149, 165, 166));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshPlaylists());

        actionPanel.add(btnViewFilms);
        actionPanel.add(btnAddFilms);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);
        playlistPanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(playlistPanel, BorderLayout.CENTER);

        refreshPlaylists();

        return panel;
    }

    /**
     * Membuat playlist baru dengan nama yang diinput user.
     * Playlist otomatis diset sebagai Private dan dimiliki oleh user yang sedang login.
     * Setelah berhasil dibuat, tabel akan di-refresh dan menampilkan pesan sukses.
     */
    private void createNewPlaylist() {
        String playlistName = JOptionPane.showInputDialog(this, "Enter playlist name:");
        if (playlistName != null && !playlistName.trim().isEmpty()) {
            String ownerEmail = AuthService.getCurrentUser().getEmail();
            Playlist newPlaylist = new Playlist(playlistName.trim(), ownerEmail, "Private", new ArrayList<>());
            playlistController.addPlaylist(newPlaylist);
            refreshPlaylists();
            ValidationUtil.showSuccess(this, "Playlist created successfully!");
        }
    }

    /**
     * Menambahkan atau mengubah film dalam playlist yang dipilih.
     * Memvalidasi bahwa user sudah memilih playlist dari tabel.
     * Menampilkan dialog untuk pemilihan film jika validasi berhasil.
     */
    private void addFilmsToPlaylist() {
        int selectedRow = playlistTable.getSelectedRow();
        if (selectedRow < 0) {
            ValidationUtil.showError(this, "Please select a playlist first!");
            return;
        }

        String playlistName = (String) playlistTableModel.getValueAt(selectedRow, 0);
        List<Playlist> userPlaylists = playlistController.getPlaylistsByOwner(AuthService.getCurrentUser().getEmail());
        Playlist selectedPlaylist = userPlaylists.stream()
                .filter(p -> p.getName().equals(playlistName))
                .findFirst()
                .orElse(null);

        if (selectedPlaylist == null) return;

        // Show film selection dialog
        showFilmSelectionDialog(selectedPlaylist);
    }

    /**
     * Menampilkan dialog untuk memilih film yang akan ditambahkan ke playlist.
     * Dialog menampilkan semua film yang visible dalam bentuk JList dengan multiple selection.
     * Film yang sudah ada dalam playlist akan otomatis ter-select.
     * User dapat menambah atau mengurangi film dengan mengubah selection.
     *
     * @param playlist objek Playlist yang akan dimodifikasi
     */
    private void showFilmSelectionDialog(Playlist playlist) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Films to " + playlist.getName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        // Available films list (only visible films)
        filmListModel = new DefaultListModel<>();
        List<Film> allFilms = filmController.getAllFilms();
        for (Film film : allFilms) {
            if (film.isVisible()) {
                filmListModel.addElement(film.getId() + " - " + film.getTitle());
            }
        }

        filmList = new JList<>(filmListModel);
        filmList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(filmList);

        // Pre-select already added films
        List<String> existingFilmIds = playlist.getFilmIds();
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < filmListModel.size(); i++) {
            String item = filmListModel.get(i);
            String filmId = item.split(" - ")[0];
            if (existingFilmIds.contains(filmId)) {
                selectedIndices.add(i);
            }
        }
        int[] indices = selectedIndices.stream().mapToInt(Integer::intValue).toArray();
        filmList.setSelectedIndices(indices);

        dialog.add(new JLabel("Select films to add to this playlist:"), BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            List<String> selectedFilmIds = new ArrayList<>();
            for (String selectedValue : filmList.getSelectedValuesList()) {
                String filmId = selectedValue.split(" - ")[0];
                selectedFilmIds.add(filmId);
            }

            playlist.setFilmIds(selectedFilmIds);
            playlistController.updatePlaylist(playlist);
            refreshPlaylists();
            dialog.dispose();
            ValidationUtil.showSuccess(this, "Playlist updated successfully!");
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Menghapus playlist yang dipilih dari tabel.
     * Memvalidasi bahwa user sudah memilih playlist dan menampilkan konfirmasi dengan detail.
     * Jika dikonfirmasi, playlist akan dihapus dan tabel di-refresh.
     */
    private void deletePlaylist() {
        int selectedRow = playlistTable.getSelectedRow();
        if (selectedRow < 0) {
            ValidationUtil.showError(this, "Pilih playlist yang ingin dihapus terlebih dahulu!");
            return;
        }

        // Get playlist details for confirmation
        String playlistName = (String) playlistTableModel.getValueAt(selectedRow, 0);
        String filmCount = playlistTableModel.getValueAt(selectedRow, 1).toString();

        String confirmMessage = String.format(
            "<html><b>Apakah Anda yakin ingin menghapus playlist ini?</b><br><br>" +
            "Nama Playlist: %s<br>" +
            "Jumlah Film: %s<br><br>" +
            "<font color='red'>Aksi ini tidak dapat dibatalkan!</font></html>",
            playlistName, filmCount
        );

        int result = JOptionPane.showConfirmDialog(this,
            confirmMessage,
            "Konfirmasi Penghapusan Playlist",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            playlistController.deletePlaylist(playlistName, AuthService.getCurrentUser().getEmail());
            refreshPlaylists();
            ValidationUtil.showSuccess(this, "Playlist berhasil dihapus!");
        }
    }

    /**
     * Merefresh tabel playlist dengan data terbaru dari database.
     * Hanya menampilkan playlist milik user yang sedang login.
     * Untuk setiap playlist, mengambil judul film berdasarkan film ID dan menampilkannya.
     */
    private void refreshPlaylists() {
        playlistTableModel.setRowCount(0);

        // Safety check: verify user is logged in before loading playlists
        if (!AuthService.isLoggedIn()) {
            return;
        }

        List<Playlist> userPlaylists = playlistController.getPlaylistsByOwner(AuthService.getCurrentUser().getEmail());

        for (Playlist playlist : userPlaylists) {
            // Get film titles from IDs
            List<String> filmTitles = new ArrayList<>();
            for (String filmId : playlist.getFilmIds()) {
                Film film = filmController.getFilmById(filmId);
                if (film != null) {
                    filmTitles.add(film.getTitle());
                }
            }

            Object[] row = {
                playlist.getName(),
                playlist.getFilmIds().size(),
                String.join(", ", filmTitles)
            };
            playlistTableModel.addRow(row);
        }
    }

    /**
     * Menampilkan dialog berisi daftar film dalam playlist yang dipilih.
     * Setiap film memiliki tombol untuk melihat detail lengkapnya.
     */
    private void viewPlaylistFilms() {
        int selectedRow = playlistTable.getSelectedRow();
        if (selectedRow < 0) {
            ValidationUtil.showError(this, "Pilih playlist terlebih dahulu!");
            return;
        }

        String playlistName = (String) playlistTableModel.getValueAt(selectedRow, 0);
        List<Playlist> userPlaylists = playlistController.getPlaylistsByOwner(AuthService.getCurrentUser().getEmail());
        Playlist selectedPlaylist = userPlaylists.stream()
                .filter(p -> p.getName().equals(playlistName))
                .findFirst()
                .orElse(null);

        if (selectedPlaylist == null || selectedPlaylist.getFilmIds().isEmpty()) {
            ValidationUtil.showError(this, "Playlist ini tidak memiliki film!");
            return;
        }

        // Create dialog to show films
        JDialog filmListDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Film dalam Playlist: " + playlistName, true);
        filmListDialog.setLayout(new BorderLayout(10, 10));
        filmListDialog.setSize(800, 500);
        filmListDialog.setLocationRelativeTo(this);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Film dalam Playlist");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel countLabel = new JLabel(selectedPlaylist.getFilmIds().size() + " Film");
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        countLabel.setForeground(new Color(220, 220, 220));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(countLabel, BorderLayout.SOUTH);

        // Content panel with films
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (String filmId : selectedPlaylist.getFilmIds()) {
            Film film = filmController.getFilmById(filmId);
            if (film != null) {
                JPanel filmItemPanel = createFilmItemPanel(film);
                contentPanel.add(filmItemPanel);
                contentPanel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnClose = new JButton("Tutup");
        btnClose.setBackground(new Color(52, 73, 94));
        btnClose.setForeground(Color.BLACK);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> filmListDialog.dispose());
        buttonPanel.add(btnClose);

        filmListDialog.add(headerPanel, BorderLayout.NORTH);
        filmListDialog.add(scrollPane, BorderLayout.CENTER);
        filmListDialog.add(buttonPanel, BorderLayout.SOUTH);

        filmListDialog.setVisible(true);
    }

    /**
     * Membuat panel item untuk setiap film dalam daftar.
     * Panel berisi informasi singkat film dan tombol untuk melihat detail.
     *
     * @param film objek Film yang akan ditampilkan
     * @return JPanel berisi informasi film dan tombol detail
     */
    private JPanel createFilmItemPanel(Film film) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Left side - Film info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel(film.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel directorLabel = new JLabel("Sutradara: " + film.getDirector());
        directorLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        directorLabel.setForeground(new Color(100, 100, 100));

        JPanel genreYearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        genreYearPanel.setBackground(new Color(245, 245, 245));

        JLabel genreLabel = new JLabel(film.getGenre());
        genreLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        genreLabel.setForeground(Color.WHITE);
        genreLabel.setBackground(new Color(231, 76, 60));
        genreLabel.setOpaque(true);
        genreLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JLabel yearLabel = new JLabel("⭐ " + film.getYear());
        yearLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        yearLabel.setForeground(new Color(241, 196, 15));

        genreYearPanel.add(genreLabel);
        genreYearPanel.add(yearLabel);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(directorLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(genreYearPanel);

        // Right side - View details button
        JButton btnViewDetail = new JButton("Lihat Detail");
        btnViewDetail.setBackground(new Color(52, 152, 219));
        btnViewDetail.setForeground(Color.BLACK);
        btnViewDetail.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnViewDetail.setFocusPainted(false);
        btnViewDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewDetail.addActionListener(e -> showFilmDetails(film));

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(btnViewDetail, BorderLayout.EAST);

        return panel;
    }

    /**
     * Menampilkan dialog detail lengkap dari sebuah film.
     * Dialog menampilkan poster, info lengkap, dan synopsis dalam layout yang menarik.
     *
     * @param film objek Film yang akan ditampilkan detailnya
     */
    private void showFilmDetails(Film film) {
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

        JLabel lblTitle = new JLabel(film.getTitle());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);

        JPanel genrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        genrePanel.setOpaque(false);

        // Genre badge
        JLabel lblGenre = new JLabel(film.getGenre());
        lblGenre.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblGenre.setForeground(Color.WHITE);
        lblGenre.setBackground(new Color(231, 76, 60));
        lblGenre.setOpaque(true);
        lblGenre.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        genrePanel.add(lblGenre);

        JLabel lblYear = new JLabel("⭐ " + film.getYear());
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

        if (film.getPosterPath() != null && !film.getPosterPath().isEmpty()) {
            try {
                String posterUrl = "https://image.tmdb.org/t/p/w500" + film.getPosterPath();
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
        JLabel lblDirectorValue = new JLabel(film.getDirector());
        lblDirectorValue.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel lblTmdbLabel = new JLabel("ID TMDB:");
        lblTmdbLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblTmdbValue = new JLabel(film.getId());
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

        JTextArea txtSynopsis = new JTextArea(film.getSynopsis());
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
