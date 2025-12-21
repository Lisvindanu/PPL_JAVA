package controller;

import model.User;
import util.FileManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk mengelola operasi CRUD data User.
 * Menangani pembacaan dan penulisan data user ke file storage.
 * Menyediakan method untuk manipulasi data user seperti add, update, delete, dan search.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class UserController {

    /**
     * Konstruktor UserController.
     * Data user dimuat dari file saat dibutuhkan (lazy loading).
     */
    public UserController() {
        // Data loaded from file when needed
    }

    /**
     * Memuat semua data user dari file storage.
     *
     * @return list semua user yang ada dalam sistem
     */
    private List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = FileManager.readLines(FileManager.USERS_FILE);
        for (String line : lines) {
            User user = User.fromFileLine(line);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Menyimpan semua data user ke file storage.
     *
     * @param users list user yang akan disimpan
     */
    private void saveUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User user : users) {
            lines.add(user.toFileLine());
        }
        FileManager.writeLines(FileManager.USERS_FILE, lines);
    }

    /**
     * Menambahkan user baru ke dalam sistem.
     *
     * @param user objek User yang akan ditambahkan
     */
    public void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    /**
     * Memperbarui data user pada index tertentu.
     *
     * @param index posisi user dalam list (0-based)
     * @param user objek User dengan data baru
     */
    public void updateUser(int index, User user) {
        List<User> users = loadUsers();
        if (index >= 0 && index < users.size()) {
            users.set(index, user);
            saveUsers(users);
        }
    }

    /**
     * Menghapus user pada index tertentu.
     *
     * @param index posisi user yang akan dihapus (0-based)
     */
    public void deleteUser(int index) {
        List<User> users = loadUsers();
        if (index >= 0 && index < users.size()) {
            users.remove(index);
            saveUsers(users);
        }
    }

    /**
     * Mendapatkan user pada index tertentu.
     *
     * @param index posisi user dalam list (0-based)
     * @return objek User jika ditemukan, null jika index tidak valid
     */
    public User getUser(int index) {
        List<User> users = loadUsers();
        if (index >= 0 && index < users.size()) {
            return users.get(index);
        }
        return null;
    }

    /**
     * Mendapatkan semua user yang ada dalam sistem.
     *
     * @return list semua user
     */
    public List<User> getAllUsers() {
        return loadUsers();
    }

    /**
     * Mencari user berdasarkan username (case-insensitive).
     *
     * @param username username yang dicari
     * @return objek User jika ditemukan, null jika tidak ada
     */
    public User findByUsername(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Mendapatkan semua user dengan status premium.
     *
     * @return list user premium
     */
    public List<User> getPremiumUsers() {
        List<User> users = loadUsers();
        List<User> results = new ArrayList<>();
        for (User user : users) {
            if (user.isPremium()) {
                results.add(user);
            }
        }
        return results;
    }

    /**
     * Menghitung total jumlah user dalam sistem.
     *
     * @return jumlah total user
     */
    public int getUserCount() {
        return loadUsers().size();
    }

    /**
     * Menghitung jumlah user dengan status premium.
     *
     * @return jumlah user premium
     */
    public int getPremiumUserCount() {
        int count = 0;
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.isPremium()) {
                count++;
            }
        }
        return count;
    }
}
