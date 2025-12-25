package util;

import javax.swing.*;
import java.awt.Component;
import java.util.regex.Pattern;

/**
 * Utility class untuk validasi input dan menampilkan dialog.
 * Menyediakan method untuk validasi email, tahun, username, password, dan string kosong.
 * Juga menyediakan method helper untuk menampilkan dialog error, success, dan konfirmasi.
 *
 * @author lisvindanu
 * @version 3.0
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[A-Za-z0-9]{3,20}$");
    private static final Pattern TMDB_ID_PATTERN =
        Pattern.compile("^[0-9]+$");

    /**
     * Memvalidasi format email menggunakan regex pattern.
     *
     * @param email string email yang akan divalidasi
     * @return true jika format email valid, false jika tidak
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Memvalidasi tahun apakah dalam rentang yang wajar (1900-2100).
     *
     * @param year string tahun yang akan divalidasi
     * @return true jika tahun valid dan dalam rentang, false jika tidak
     */
    public static boolean isValidYear(String year) {
        try {
            int yearInt = Integer.parseInt(year);
            return yearInt >= 1900 && yearInt <= 2100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Mengecek apakah string kosong atau null.
     *
     * @param text string yang akan dicek
     * @return true jika null atau kosong (setelah di-trim), false jika berisi teks
     */
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Menampilkan dialog error dengan pesan tertentu.
     *
     * @param parent komponen parent untuk dialog
     * @param message pesan error yang akan ditampilkan
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Menampilkan dialog success dengan pesan tertentu.
     *
     * @param parent komponen parent untuk dialog
     * @param message pesan success yang akan ditampilkan
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Menampilkan dialog konfirmasi dengan opsi Yes/No.
     *
     * @param parent komponen parent untuk dialog
     * @param message pesan konfirmasi yang akan ditampilkan
     * @return true jika user memilih Yes, false jika No
     */
    public static boolean confirmAction(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message,
            "Confirmation", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Memvalidasi format username (alphanumeric, 3-20 karakter).
     *
     * @param username string username yang akan divalidasi
     * @return true jika username valid, false jika tidak
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Memvalidasi kekuatan password (minimal 8 karakter, harus ada uppercase, lowercase, dan angka).
     *
     * @param password string password yang akan divalidasi
     * @return true jika password memenuhi kriteria kuat, false jika tidak
     */
    public static boolean isValidPasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        return hasUppercase && hasLowercase && hasDigit;
    }

    /**
     * Mendapatkan deskripsi kekuatan password.
     *
     * @param password string password yang akan dicek
     * @return deskripsi kekuatan: "Lemah", "Sedang", "Kuat", atau "Sangat Kuat"
     */
    public static String getPasswordStrengthDescription(String password) {
        if (password == null || password.isEmpty()) {
            return "Lemah";
        }

        int score = 0;

        // Check length
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Check character types
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch))) score++;

        if (score <= 2) return "Lemah";
        if (score <= 4) return "Sedang";
        if (score == 5) return "Kuat";
        return "Sangat Kuat";
    }

    /**
     * Memvalidasi TMDB ID (harus berupa angka).
     *
     * @param tmdbId string TMDB ID yang akan divalidasi
     * @return true jika TMDB ID valid (hanya angka), false jika tidak
     */
    public static boolean isValidTMDBId(String tmdbId) {
        return tmdbId != null && TMDB_ID_PATTERN.matcher(tmdbId).matches();
    }

    /**
     * Memvalidasi panjang string dengan batas minimum dan maksimum.
     *
     * @param text string yang akan divalidasi
     * @param minLength panjang minimum
     * @param maxLength panjang maksimum
     * @return true jika panjang string dalam rentang, false jika tidak
     */
    public static boolean isValidStringLength(String text, int minLength, int maxLength) {
        if (text == null) return false;
        int length = text.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Menampilkan dialog error dengan custom title.
     *
     * @param parent komponen parent untuk dialog
     * @param message pesan error yang akan ditampilkan
     * @param title custom title untuk dialog
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Menampilkan dialog error validasi dengan nama field.
     *
     * @param parent komponen parent untuk dialog
     * @param fieldName nama field yang error
     * @param errorMessage pesan error spesifik
     */
    public static void showValidationError(Component parent, String fieldName, String errorMessage) {
        String message = fieldName + ": " + errorMessage;
        JOptionPane.showMessageDialog(parent, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}
