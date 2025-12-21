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

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddFilms = new JButton("Add Films to Selected Playlist");
        JButton btnDelete = new JButton("Delete Playlist");
        JButton btnRefresh = new JButton("Refresh");

        btnAddFilms.addActionListener(e -> addFilmsToPlaylist());
        btnDelete.addActionListener(e -> deletePlaylist());
        btnRefresh.addActionListener(e -> refreshPlaylists());

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
     * Memvalidasi bahwa user sudah memilih playlist dan menampilkan konfirmasi.
     * Jika dikonfirmasi, playlist akan dihapus dan tabel di-refresh.
     */
    private void deletePlaylist() {
        int selectedRow = playlistTable.getSelectedRow();
        if (selectedRow < 0) {
            ValidationUtil.showError(this, "Please select a playlist to delete!");
            return;
        }

        if (ValidationUtil.confirmAction(this, "Are you sure you want to delete this playlist?")) {
            String playlistName = (String) playlistTableModel.getValueAt(selectedRow, 0);
            playlistController.deletePlaylist(playlistName, AuthService.getCurrentUser().getEmail());
            refreshPlaylists();
            ValidationUtil.showSuccess(this, "Playlist deleted successfully!");
        }
    }

    /**
     * Merefresh tabel playlist dengan data terbaru dari database.
     * Hanya menampilkan playlist milik user yang sedang login.
     * Untuk setiap playlist, mengambil judul film berdasarkan film ID dan menampilkannya.
     */
    private void refreshPlaylists() {
        playlistTableModel.setRowCount(0);
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
}
