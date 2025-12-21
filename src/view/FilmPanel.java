package view;

import model.Film;
import controller.FilmController;
import util.InputUtil;
import util.ValidationUtil;
import util.TMDBService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel untuk manajemen film oleh admin.
 * Menyediakan form untuk menambah film baik dari TMDB API maupun manual input.
 * Admin dapat mengatur visibility film dan menghapus film dari sistem.
 * Menampilkan semua film dalam tabel dengan informasi lengkap.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class FilmPanel extends JPanel {
    private JTextField txtMovieId;
    private JTextField txtTitle, txtDirector, txtYear;
    private JTextArea txtSynopsis;
    private JComboBox<String> cmbGenre;
    private JTable table;
    private DefaultTableModel tableModel;
    private FilmController filmController;
    private JCheckBox chkManualInput;
    private JButton btnFetch;
    private JRadioButton rbVisible, rbHidden;
    private ButtonGroup visibilityGroup;

    /**
     * Konstruktor FilmPanel.
     * Menginisialisasi controller dan membuat UI dengan form input dan tabel film.
     *
     * @param controller controller untuk mengelola data film
     */
    public FilmPanel(FilmController controller) {
        this.filmController = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Membuat panel form untuk registrasi film.
     * Form mendukung dua mode: TMDB fetch mode dan manual input mode.
     * Berisi field untuk movie ID, title, director, genre, year, synopsis, dan visibility.
     *
     * @return JPanel form dengan semua komponen input
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Film Registration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Manual Input Checkbox
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        chkManualInput = new JCheckBox("Manual Input (without TMDB)");
        chkManualInput.addActionListener(e -> toggleInputMode());
        panel.add(chkManualInput, gbc);
        gbc.gridwidth = 1;

        // TMDB Movie ID with Fetch Button
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("TMDB Movie ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMovieId = new JTextField(15);
        panel.add(txtMovieId, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        btnFetch = new JButton("Fetch Data");
        btnFetch.addActionListener(e -> fetchMovieData());
        panel.add(btnFetch, gbc);

        // Title (read-only after fetch)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        txtTitle = new JTextField(20);
        txtTitle.setEditable(false);
        panel.add(txtTitle, gbc);
        gbc.gridwidth = 1;

        // Director (read-only)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Director:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        txtDirector = new JTextField(20);
        txtDirector.setEditable(false);
        panel.add(txtDirector, gbc);
        gbc.gridwidth = 1;

        // Genre (JComboBox - read-only)
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Genre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        String[] genres = {"Action", "Drama", "Comedy", "Horror", "Sci-Fi", "Romance", "Thriller"};
        cmbGenre = new JComboBox<>(genres);
        cmbGenre.setEnabled(false);
        panel.add(cmbGenre, gbc);
        gbc.gridwidth = 1;

        // Year (read-only)
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        txtYear = new JTextField(20);
        txtYear.setEditable(false);
        panel.add(txtYear, gbc);
        gbc.gridwidth = 1;

        // Synopsis (JTextArea - read-only)
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("Synopsis:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        txtSynopsis = new JTextArea(4, 20);
        txtSynopsis.setLineWrap(true);
        txtSynopsis.setWrapStyleWord(true);
        txtSynopsis.setEditable(false);
        JScrollPane scrollSynopsis = new JScrollPane(txtSynopsis);
        panel.add(scrollSynopsis, gbc);
        gbc.gridwidth = 1;

        // Visibility (JRadioButton)
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        panel.add(new JLabel("Visibility:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 2;
        JPanel visibilityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbVisible = new JRadioButton("Show to Users", true);
        rbHidden = new JRadioButton("Hide from Users");
        visibilityGroup = new ButtonGroup();
        visibilityGroup.add(rbVisible);
        visibilityGroup.add(rbHidden);
        visibilityPanel.add(rbVisible);
        visibilityPanel.add(rbHidden);
        panel.add(visibilityPanel, gbc);
        gbc.gridwidth = 1;

        // Buttons
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAdd = new JButton("Add Film");
        JButton btnClear = new JButton("Clear");
        JButton btnDelete = new JButton("Delete Selected");

        btnAdd.addActionListener(e -> addFilm());
        btnClear.addActionListener(e -> clearForm());
        btnDelete.addActionListener(e -> deleteFilm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnClear);
        btnPanel.add(btnDelete);
        panel.add(btnPanel, gbc);

        return panel;
    }

    /**
     * Toggle antara TMDB fetch mode dan manual input mode.
     * Dalam TMDB mode, field input disabled dan hanya bisa diisi via fetch.
     * Dalam manual mode, semua field dapat diedit langsung dan ID digenerate otomatis.
     */
    private void toggleInputMode() {
        boolean manualMode = chkManualInput.isSelected();

        // Toggle TMDB fields
        txtMovieId.setEditable(!manualMode);
        btnFetch.setEnabled(!manualMode);

        // Toggle manual input fields
        txtTitle.setEditable(manualMode);
        txtDirector.setEditable(manualMode);
        txtYear.setEditable(manualMode);
        txtSynopsis.setEditable(manualMode);
        cmbGenre.setEnabled(manualMode);

        // Clear form when switching modes
        clearForm();

        // In manual mode, generate a random ID
        if (manualMode) {
            txtMovieId.setText("MANUAL-" + System.currentTimeMillis());
        }
    }

    /**
     * Mengambil data film dari TMDB API berdasarkan movie ID yang diinput.
     * Menggunakan SwingWorker untuk async operation agar UI tidak freeze.
     * Menampilkan loading dialog selama proses fetching.
     * Setelah berhasil, semua field form akan terisi otomatis dengan data dari TMDB.
     */
    private void fetchMovieData() {
        try {
            String movieId = InputUtil.getTextField(txtMovieId, "TMDB Movie ID");

            // Show loading
            JDialog loadingDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Loading...", true);
            JLabel loadingLabel = new JLabel("Fetching movie data from TMDB...", SwingConstants.CENTER);
            loadingDialog.add(loadingLabel);
            loadingDialog.setSize(300, 100);
            loadingDialog.setLocationRelativeTo(this);

            SwingWorker<TMDBService.MovieData, Void> worker = new SwingWorker<>() {
                @Override
                protected TMDBService.MovieData doInBackground() throws Exception {
                    return TMDBService.fetchMovieData(movieId);
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    try {
                        TMDBService.MovieData movieData = get();
                        txtTitle.setText(movieData.title);
                        txtDirector.setText(movieData.director);
                        txtYear.setText(String.valueOf(movieData.year));
                        txtSynopsis.setText(movieData.synopsis);

                        // Set genre in combobox
                        for (int i = 0; i < cmbGenre.getItemCount(); i++) {
                            if (cmbGenre.getItemAt(i).equalsIgnoreCase(movieData.genre)) {
                                cmbGenre.setSelectedIndex(i);
                                break;
                            }
                        }

                        ValidationUtil.showSuccess(FilmPanel.this, "Movie data fetched successfully!");
                    } catch (Exception ex) {
                        ValidationUtil.showError(FilmPanel.this, "Failed to fetch movie data: " + ex.getMessage());
                    }
                }
            };

            worker.execute();
            loadingDialog.setVisible(true);

        } catch (IllegalArgumentException ex) {
            ValidationUtil.showError(this, ex.getMessage());
        }
    }

    /**
     * Membuat panel tabel untuk menampilkan daftar semua film.
     * Tabel menampilkan ID, title, director, genre, year, synopsis, dan status visibility.
     * Tabel tidak dapat diedit dan hanya mendukung single selection.
     *
     * @return JPanel yang berisi tabel film
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Film List"));

        String[] columns = {"ID", "Title", "Director", "Genre", "Year", "Synopsis", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load existing films
        refreshTable();

        return panel;
    }

    /**
     * Merefresh tabel film dengan data terbaru dari database.
     * Memuat ulang semua film termasuk yang hidden.
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        filmController.getAllFilms().forEach(film -> tableModel.addRow(film.toTableRow()));
    }

    /**
     * Menambahkan film baru ke database berdasarkan data di form.
     * Melakukan validasi lengkap untuk semua field yang required.
     * Validasi berbeda antara TMDB mode dan manual mode.
     * Setelah berhasil ditambahkan, tabel akan di-refresh dan form dikosongkan.
     */
    private void addFilm() {
        try {
            // Validate required fields
            if (txtTitle.getText().trim().isEmpty()) {
                ValidationUtil.showError(this, chkManualInput.isSelected() ?
                    "Please fill in the title!" : "Please fetch movie data first!");
                return;
            }

            String movieId = txtMovieId.getText().trim();
            String title = txtTitle.getText().trim();
            String director = txtDirector.getText().trim();
            String genre = (String) cmbGenre.getSelectedItem();

            // Validate year field
            if (txtYear.getText().trim().isEmpty()) {
                ValidationUtil.showError(this, "Please enter the year!");
                return;
            }
            int year = Integer.parseInt(txtYear.getText().trim());

            String synopsis = txtSynopsis.getText().trim();

            // Additional validation for manual input
            if (chkManualInput.isSelected()) {
                if (director.isEmpty()) {
                    ValidationUtil.showError(this, "Please enter the director!");
                    return;
                }
                if (synopsis.isEmpty()) {
                    ValidationUtil.showError(this, "Please enter the synopsis!");
                    return;
                }
            }

            // Get visibility from radio button
            boolean isVisible = rbVisible.isSelected();

            Film film = new Film(movieId, title, director, genre, year, synopsis, isVisible);
            filmController.addFilm(film);
            refreshTable();
            clearForm();
            ValidationUtil.showSuccess(this, "Film added successfully!");
        } catch (NumberFormatException ex) {
            ValidationUtil.showError(this, "Year must be a valid number!");
        } catch (Exception ex) {
            ValidationUtil.showError(this, ex.getMessage());
        }
    }

    /**
     * Membersihkan semua field input pada form.
     * Mengembalikan form ke kondisi awal siap untuk input baru.
     * Jika dalam manual mode, akan generate ID baru otomatis.
     */
    private void clearForm() {
        InputUtil.clearTextField(txtMovieId, txtTitle, txtDirector, txtYear);
        InputUtil.clearTextArea(txtSynopsis);
        cmbGenre.setSelectedIndex(0);
        rbVisible.setSelected(true); // Default to visible

        // Reset manual ID if in manual mode
        if (chkManualInput.isSelected()) {
            txtMovieId.setText("MANUAL-" + System.currentTimeMillis());
        }
    }

    /**
     * Menghapus film yang dipilih dari tabel.
     * Memvalidasi bahwa user sudah memilih film dan menampilkan konfirmasi.
     * Jika dikonfirmasi, film akan dihapus dari database dan tabel di-refresh.
     */
    private void deleteFilm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            if (ValidationUtil.confirmAction(this, "Are you sure you want to delete this film?")) {
                filmController.deleteFilm(selectedRow);
                refreshTable();
                ValidationUtil.showSuccess(this, "Film deleted successfully!");
            }
        } else {
            ValidationUtil.showError(this, "Please select a film to delete!");
        }
    }
}
