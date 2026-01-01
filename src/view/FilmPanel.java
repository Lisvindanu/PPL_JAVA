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
    private String currentPosterPath; // Store poster path from TMDB fetch

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

        // Buttons with styling
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton btnAdd = new JButton("Add Film");
        JButton btnClear = new JButton("Clear");
        JButton btnDelete = new JButton("Delete Selected");

        // Style Add button (Green)
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.setFocusPainted(false);
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> addFilm());

        // Style Clear button (Blue)
        btnClear.setBackground(new Color(52, 152, 219));
        btnClear.setForeground(Color.BLACK);
        btnClear.setFocusPainted(false);
        btnClear.setOpaque(true);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> clearForm());

        // Style Delete button (Red)
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.setFocusPainted(false);
        btnDelete.setOpaque(true);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
     * Menampilkan loading dialog dengan progress bar selama proses fetching.
     * Setelah berhasil, semua field form akan terisi otomatis dengan data dari TMDB.
     */
    private void fetchMovieData() {
        try {
            String movieId = InputUtil.getTextField(txtMovieId, "TMDB Movie ID");

            // Validate TMDB ID format
            if (!ValidationUtil.isValidTMDBId(movieId)) {
                ValidationUtil.showValidationError(this, "TMDB Movie ID", "Harus berupa angka");
                return;
            }

            // Show enhanced loading dialog
            JDialog loadingDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Loading...", true);
            loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            JPanel loadingPanel = new JPanel(new BorderLayout(10, 10));
            loadingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel loadingLabel = new JLabel("Mengambil data dari TMDB...", SwingConstants.CENTER);
            loadingLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);

            loadingPanel.add(loadingLabel, BorderLayout.NORTH);
            loadingPanel.add(progressBar, BorderLayout.CENTER);

            loadingDialog.add(loadingPanel);
            loadingDialog.setSize(350, 120);
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
                        currentPosterPath = movieData.posterPath; // Store poster path

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
     * Melakukan validasi lengkap untuk semua field yang required dengan ValidationUtil.
     * Validasi berbeda antara TMDB mode dan manual mode.
     * Setelah berhasil ditambahkan, tabel akan di-refresh dan form dikosongkan.
     */
    private void addFilm() {
        try {
            // Get and validate movie ID
            String movieId = InputUtil.getTextField(txtMovieId, "Movie ID");

            // Get and validate title with length check
            String title = InputUtil.getTextField(txtTitle, "Title");
            if (!ValidationUtil.isValidStringLength(title, 1, 200)) {
                ValidationUtil.showValidationError(this, "Title",
                    "Panjang judul harus antara 1-200 karakter");
                return;
            }

            // Get and validate director
            String director = InputUtil.getTextField(txtDirector, "Director");
            if (!ValidationUtil.isValidStringLength(director, 1, 100)) {
                ValidationUtil.showValidationError(this, "Director",
                    "Panjang nama sutradara harus antara 1-100 karakter");
                return;
            }

            String genre = (String) cmbGenre.getSelectedItem();

            // Get and validate year with ValidationUtil.isValidYear()
            String yearStr = InputUtil.getTextField(txtYear, "Year");
            if (!ValidationUtil.isValidYear(yearStr)) {
                ValidationUtil.showValidationError(this, "Year",
                    "Tahun harus antara 1900-2100");
                return;
            }
            int year = InputUtil.getIntField(txtYear, "Year");

            // Get and validate synopsis with length check
            String synopsis = InputUtil.getTextArea(txtSynopsis, "Synopsis");
            if (!ValidationUtil.isValidStringLength(synopsis, 10, 2000)) {
                ValidationUtil.showValidationError(this, "Synopsis",
                    "Panjang sinopsis harus antara 10-2000 karakter");
                return;
            }

            // Get visibility from radio button
            boolean isVisible = rbVisible.isSelected();

            // Use stored posterPath from TMDB fetch, or empty string if manual input
            String posterPath = (currentPosterPath != null) ? currentPosterPath : "";

            Film film = new Film(movieId, title, director, genre, year, synopsis, posterPath, isVisible);
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
        currentPosterPath = null; // Clear poster path

        // Reset manual ID if in manual mode
        if (chkManualInput.isSelected()) {
            txtMovieId.setText("MANUAL-" + System.currentTimeMillis());
        }
    }

    /**
     * Menghapus film yang dipilih dari tabel.
     * Memvalidasi bahwa user sudah memilih film dan menampilkan konfirmasi dengan detail film.
     * Jika dikonfirmasi, film akan dihapus dari database dan tabel di-refresh.
     */
    private void deleteFilm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // Get film details for confirmation
            String filmTitle = (String) tableModel.getValueAt(selectedRow, 1);
            String filmDirector = (String) tableModel.getValueAt(selectedRow, 2);
            String filmYear = tableModel.getValueAt(selectedRow, 4).toString();

            String confirmMessage = String.format(
                "<html><b>Apakah Anda yakin ingin menghapus film ini?</b><br><br>" +
                "Judul: %s<br>" +
                "Sutradara: %s<br>" +
                "Tahun: %s<br><br>" +
                "<font color='red'>Aksi ini tidak dapat dibatalkan!</font></html>",
                filmTitle, filmDirector, filmYear
            );

            int result = JOptionPane.showConfirmDialog(this,
                confirmMessage,
                "Konfirmasi Penghapusan Film",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                filmController.deleteFilm(selectedRow);
                refreshTable();
                ValidationUtil.showSuccess(this, "Film berhasil dihapus!");
            }
        } else {
            ValidationUtil.showError(this, "Pilih film yang ingin dihapus terlebih dahulu!");
        }
    }
}
