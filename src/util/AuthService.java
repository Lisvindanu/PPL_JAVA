package util;

import model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Service untuk mengelola autentikasi dan autorisasi pengguna.
 * Menangani proses login, logout, register, dan manajemen session pengguna.
 * Menyediakan method untuk pengecekan role dan akses pengguna.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class AuthService {
    private static User currentUser = null;
    private static final String DEFAULT_ADMIN_EMAIL = "anaphygon@protonmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "password";

    static {
        initializeDefaultAdmin();
    }

    /**
     * Menginisialisasi akun admin default jika belum ada.
     * Dipanggil secara otomatis saat class dimuat.
     */
    private static void initializeDefaultAdmin() {
        List<String> users = FileManager.readLines(FileManager.USERS_FILE);
        boolean adminExists = users.stream().anyMatch(line -> line.startsWith(DEFAULT_ADMIN_EMAIL));

        if (!adminExists) {
            User admin = new User(DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD, "Admin", "ADMIN");
            FileManager.appendLine(FileManager.USERS_FILE, admin.toFileLine());
        }
    }

    /**
     * Melakukan login pengguna berdasarkan email dan password.
     * Jika berhasil, user tersebut akan disimpan sebagai current user.
     *
     * @param email email pengguna
     * @param password password pengguna
     * @return objek User jika login berhasil, null jika gagal
     */
    public static User login(String email, String password) {
        List<String> users = FileManager.readLines(FileManager.USERS_FILE);

        for (String line : users) {
            User user = User.fromFileLine(line);
            if (user != null && user.getEmail().equals(email) && user.getPassword().equals(password)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    /**
     * Mendaftarkan pengguna baru ke dalam sistem.
     * Email harus unik dan belum terdaftar sebelumnya.
     *
     * @param email email pengguna baru
     * @param password password pengguna baru
     * @param username username pengguna baru
     * @return true jika registrasi berhasil, false jika email sudah ada
     */
    public static boolean register(String email, String password, String username) {
        // Check if email already exists
        List<String> users = FileManager.readLines(FileManager.USERS_FILE);
        for (String line : users) {
            if (line.startsWith(email + "|")) {
                return false; // Email already exists
            }
        }

        // Create new user with USER role
        User newUser = new User(email, password, username, "USER");
        FileManager.appendLine(FileManager.USERS_FILE, newUser.toFileLine());
        return true;
    }

    /**
     * Melakukan logout pengguna yang sedang login.
     * Menghapus current user dari session.
     */
    public static void logout() {
        currentUser = null;
    }

    /**
     * Mendapatkan objek user yang sedang login.
     *
     * @return objek User yang sedang login, atau null jika belum login
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Mengecek apakah ada pengguna yang sedang login.
     *
     * @return true jika ada user yang login, false jika belum
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Mengecek apakah pengguna yang sedang login adalah admin.
     *
     * @return true jika user adalah admin, false jika bukan atau belum login
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /**
     * Mendapatkan semua pengguna yang terdaftar dalam sistem.
     *
     * @return list semua user
     */
    public static List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        List<String> lines = FileManager.readLines(FileManager.USERS_FILE);

        for (String line : lines) {
            User user = User.fromFileLine(line);
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }

    /**
     * Memperbarui data user yang sudah ada dalam sistem.
     * User diidentifikasi berdasarkan email.
     *
     * @param user objek User dengan data baru
     */
    public static void updateUser(User user) {
        List<String> lines = FileManager.readLines(FileManager.USERS_FILE);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            User u = User.fromFileLine(line);
            if (u != null && u.getEmail().equals(user.getEmail())) {
                updatedLines.add(user.toFileLine());
            } else {
                updatedLines.add(line);
            }
        }

        FileManager.writeLines(FileManager.USERS_FILE, updatedLines);
    }
}
