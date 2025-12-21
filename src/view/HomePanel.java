package view;

import controller.FilmController;
import controller.PlaylistController;
import model.Film;
import util.AuthService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + AuthService.getCurrentUser().getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Film Stats Card
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel filmCard = createStatsCard("Available Films", "0", new Color(52, 152, 219));
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

        // Refresh button
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshData());
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Merefresh data tabel film dari database.
     * Memuat ulang semua film yang visible dan update statistik.
     */
    public void refreshData() {
        // Clear table
        filmTableModel.setRowCount(0);

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
}
