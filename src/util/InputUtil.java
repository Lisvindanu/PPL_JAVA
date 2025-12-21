package util;

import javax.swing.*;

/**
 * Utility class untuk mengambil dan membersihkan input dari komponen Swing.
 * Menyediakan method helper untuk mendapatkan nilai dari JTextField dan JTextArea.
 * Melakukan validasi otomatis bahwa field tidak kosong.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class InputUtil {

    /**
     * Mendapatkan teks dari JTextField dengan validasi tidak kosong.
     *
     * @param field JTextField yang akan diambil nilainya
     * @param fieldName nama field untuk pesan error
     * @return nilai teks dari field (sudah di-trim)
     * @throws IllegalArgumentException jika field kosong
     */
    public static String getTextField(JTextField field, String fieldName) {
        String value = field.getText().trim();
        if (ValidationUtil.isEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty!");
        }
        return value;
    }

    /**
     * Mendapatkan teks dari JTextArea dengan validasi tidak kosong.
     *
     * @param area JTextArea yang akan diambil nilainya
     * @param fieldName nama field untuk pesan error
     * @return nilai teks dari area (sudah di-trim)
     * @throws IllegalArgumentException jika area kosong
     */
    public static String getTextArea(JTextArea area, String fieldName) {
        String value = area.getText().trim();
        if (ValidationUtil.isEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty!");
        }
        return value;
    }

    /**
     * Mendapatkan nilai integer dari JTextField dengan validasi.
     *
     * @param field JTextField yang berisi angka
     * @param fieldName nama field untuk pesan error
     * @return nilai integer dari field
     * @throws IllegalArgumentException jika field kosong atau bukan angka valid
     */
    public static int getIntField(JTextField field, String fieldName) {
        String value = field.getText().trim();
        if (ValidationUtil.isEmpty(value)) {
            throw new IllegalArgumentException(fieldName + " cannot be empty!");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number!");
        }
    }

    /**
     * Membersihkan (mengosongkan) satu atau lebih JTextField.
     *
     * @param fields varargs JTextField yang akan dibersihkan
     */
    public static void clearTextField(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    /**
     * Membersihkan (mengosongkan) satu atau lebih JTextArea.
     *
     * @param areas varargs JTextArea yang akan dibersihkan
     */
    public static void clearTextArea(JTextArea... areas) {
        for (JTextArea area : areas) {
            area.setText("");
        }
    }
}
