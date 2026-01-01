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
