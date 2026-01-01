package util;

import javax.swing.*;
import java.util.regex.Pattern;

/**
 * Utility class untuk validasi input dan menampilkan dialog.
 * Menyediakan method untuk validasi email, tahun, dan string kosong.
 * Juga menyediakan method helper untuk menampilkan dialog error, success, dan konfirmasi.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

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
     * Memvalidasi tahun apakah dalam rentang yang wajar (1900-2100).
     *
     * @param year int tahun yang akan divalidasi
     * @return true jika tahun valid dan dalam rentang, false jika tidak
     */
    public static boolean isValidYear(int year) {
        return year >= 1900 && year <= 2100;
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
     * Validasi username (alphanumeric, 3-20 karakter).
     *
     * @param username username yang divalidasi
     * @return true jika valid
     */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        return username.matches("^[a-zA-Z0-9]{3,20}$");
    }

    /**
     * Validasi password strength (min 8, huruf besar, huruf kecil, angka).
     *
     * @param password password yang divalidasi
     * @return true jika kuat
     */
    public static boolean isValidPasswordStrength(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Mendapatkan deskripsi kekuatan password.
     *
     * @param password password yang dicek
     * @return deskripsi kekuatan
     */
    public static String getPasswordStrengthDescription(String password) {
        if (password == null || password.length() < 6) return "Lemah";
        
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        int score = 0;
        if (password.length() >= 8) score++;
        if (hasUpper && hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        
        if (score <= 1) return "Sedang";
        if (score <= 3) return "Kuat";
        return "Sangat Kuat";
    }

    /**
     * Validasi TMDB ID (hanya angka).
     *
     * @param tmdbId ID yang divalidasi
     * @return true jika hanya angka
     */
    public static boolean isValidTMDBId(String tmdbId) {
        if (tmdbId == null || tmdbId.isEmpty()) return false;
        return tmdbId.matches("^[0-9]+$");
    }

    /**
     * Validasi panjang string.
     *
     * @param text string yang divalidasi
     * @param min panjang minimum
     * @param max panjang maksimum
     * @return true jika dalam rentang
     */
    public static boolean isValidStringLength(String text, int min, int max) {
        if (text == null) return false;
        int len = text.length();
        return len >= min && len <= max;
    }

    /**
     * Menampilkan dialog error dengan pesan tertentu.
     *
     * @param parent komponen parent untuk dialog
     * @param message pesan error yang akan ditampilkan
     */
    public static void showError(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Menampilkan dialog success dengan pesan tertentu.
     *
     * @param parent komponen parent untuk dialog
     * @param message pesan success yang akan ditampilkan
     */
    public static void showSuccess(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Menampilkan dialog konfirmasi dengan opsi Yes/No.
     *
     * @param parent komponen parent untuk dialog
     * @param message pesan konfirmasi yang akan ditampilkan
     * @return true jika user memilih Yes, false jika No
     */
    public static boolean confirmAction(JComponent parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message,
            "Confirmation", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
