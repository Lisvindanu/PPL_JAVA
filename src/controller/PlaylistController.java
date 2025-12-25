package controller;

import model.Playlist;
import util.FileManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller untuk mengelola operasi CRUD data Playlist.
 * Menangani pembacaan dan penulisan data playlist ke file storage.
 * Menyediakan method untuk manipulasi playlist seperti add, update, delete, dan query berdasarkan owner.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class PlaylistController {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);

    /**
     * Konstruktor PlaylistController.
     * Data playlist dimuat dari file saat dibutuhkan (lazy loading).
     */
    public PlaylistController() {
        // Data loaded from file when needed
    }

    /**
     * Memuat semua data playlist dari file storage.
     *
     * @return list semua playlist yang ada dalam sistem
     */
    private List<Playlist> loadPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        List<String> lines = FileManager.readLines(FileManager.PLAYLISTS_FILE);
        for (String line : lines) {
            Playlist playlist = Playlist.fromFileLine(line);
            if (playlist != null) {
                playlists.add(playlist);
            }
        }
        return playlists;
    }

    /**
     * Menyimpan semua data playlist ke file storage.
     *
     * @param playlists list playlist yang akan disimpan
     */
    private void savePlaylists(List<Playlist> playlists) {
        List<String> lines = new ArrayList<>();
        for (Playlist playlist : playlists) {
            lines.add(playlist.toFileLine());
        }
        FileManager.writeLines(FileManager.PLAYLISTS_FILE, lines);
    }

    /**
     * Menambahkan playlist baru ke dalam sistem.
     *
     * @param playlist objek Playlist yang akan ditambahkan
     */
    public void addPlaylist(Playlist playlist) {
        logger.info("Adding new playlist: {} for user: {}", playlist.getName(), playlist.getOwnerEmail());
        List<Playlist> playlists = loadPlaylists();
        playlists.add(playlist);
        savePlaylists(playlists);
        logger.info("Playlist added successfully: {}", playlist.getName());
    }

    /**
     * Memperbarui data playlist yang sudah ada.
     * Playlist diidentifikasi berdasarkan nama dan owner email.
     *
     * @param playlist objek Playlist dengan data baru
     */
    public void updatePlaylist(Playlist playlist) {
        logger.info("Updating playlist: {} for user: {}", playlist.getName(), playlist.getOwnerEmail());
        List<Playlist> playlists = loadPlaylists();
        List<Playlist> updated = new ArrayList<>();
        boolean found = false;
        for (Playlist p : playlists) {
            if (p.getName().equals(playlist.getName()) && p.getOwnerEmail().equals(playlist.getOwnerEmail())) {
                updated.add(playlist);
                found = true;
            } else {
                updated.add(p);
            }
        }
        savePlaylists(updated);
        if (found) {
            logger.info("Playlist updated successfully: {}", playlist.getName());
        } else {
            logger.warn("Failed to update playlist. Not found: {}", playlist.getName());
        }
    }

    /**
     * Menghapus playlist berdasarkan nama dan owner email.
     *
     * @param playlistName nama playlist yang akan dihapus
     * @param ownerEmail email pemilik playlist
     */
    public void deletePlaylist(String playlistName, String ownerEmail) {
        logger.info("Deleting playlist: {} for user: {}", playlistName, ownerEmail);
        List<Playlist> playlists = loadPlaylists();
        boolean removed = playlists.removeIf(p -> p.getName().equals(playlistName) && p.getOwnerEmail().equals(ownerEmail));
        savePlaylists(playlists);
        if (removed) {
            logger.info("Playlist deleted successfully: {}", playlistName);
        } else {
            logger.warn("Failed to delete playlist. Not found: {}", playlistName);
        }
    }

    /**
     * Mendapatkan semua playlist yang ada dalam sistem.
     *
     * @return list semua playlist
     */
    public List<Playlist> getAllPlaylists() {
        return loadPlaylists();
    }

    /**
     * Mendapatkan semua playlist milik user tertentu berdasarkan email.
     *
     * @param ownerEmail email pemilik playlist
     * @return list playlist milik user tersebut
     */
    public List<Playlist> getPlaylistsByOwner(String ownerEmail) {
        return loadPlaylists().stream()
                .filter(p -> p.getOwnerEmail().equals(ownerEmail))
                .collect(Collectors.toList());
    }

    /**
     * Menghitung total jumlah playlist dalam sistem.
     *
     * @return jumlah total playlist
     */
    public int getPlaylistCount() {
        return loadPlaylists().size();
    }
}
