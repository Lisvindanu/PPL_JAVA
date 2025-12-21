package model;

import java.util.List;

/**
 * Kelas model yang merepresentasikan data pengguna dalam sistem.
 * Menyimpan informasi pengguna termasuk kredensial, profil, dan role.
 * Mendukung serialisasi dan deserialisasi untuk penyimpanan file.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class User {
    private String email;
    private String password;
    private String username;
    private String role; // "ADMIN" or "USER"
    private String gender;
    private boolean isPremium;

    /**
     * Konstruktor untuk membuat User dengan informasi dasar autentikasi.
     * Gender dan status premium diset ke nilai default.
     *
     * @param email email pengguna sebagai identifier unik
     * @param password password pengguna (plaintext)
     * @param username nama tampilan pengguna
     * @param role role pengguna ("ADMIN" atau "USER")
     */
    public User(String email, String password, String username, String role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.gender = "";
        this.isPremium = false;
    }

    /**
     * Konstruktor lengkap untuk membuat User dengan semua informasi.
     *
     * @param email email pengguna sebagai identifier unik
     * @param password password pengguna (plaintext)
     * @param username nama tampilan pengguna
     * @param role role pengguna ("ADMIN" atau "USER")
     * @param gender jenis kelamin pengguna
     * @param isPremium status premium pengguna
     */
    public User(String email, String password, String username, String role,
                String gender, boolean isPremium) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.gender = gender;
        this.isPremium = isPremium;
    }

    /**
     * Mendapatkan username pengguna.
     *
     * @return username pengguna
     */
    public String getUsername() { return username; }

    /**
     * Mengatur username pengguna.
     *
     * @param username username baru
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Mendapatkan email pengguna.
     *
     * @return email pengguna
     */
    public String getEmail() { return email; }

    /**
     * Mengatur email pengguna.
     *
     * @param email email baru
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Mendapatkan jenis kelamin pengguna.
     *
     * @return jenis kelamin pengguna
     */
    public String getGender() { return gender; }

    /**
     * Mengatur jenis kelamin pengguna.
     *
     * @param gender jenis kelamin baru
     */
    public void setGender(String gender) { this.gender = gender; }

    /**
     * Mengecek apakah pengguna memiliki akun premium.
     *
     * @return true jika premium, false jika tidak
     */
    public boolean isPremium() { return isPremium; }

    /**
     * Mengatur status premium pengguna.
     *
     * @param premium status premium baru
     */
    public void setPremium(boolean premium) { isPremium = premium; }

    /**
     * Mendapatkan password pengguna.
     *
     * @return password pengguna
     */
    public String getPassword() { return password; }

    /**
     * Mengatur password pengguna.
     *
     * @param password password baru
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Mendapatkan role pengguna.
     *
     * @return role pengguna ("ADMIN" atau "USER")
     */
    public String getRole() { return role; }

    /**
     * Mengatur role pengguna.
     *
     * @param role role baru
     */
    public void setRole(String role) { this.role = role; }

    /**
     * Mengecek apakah pengguna adalah admin.
     *
     * @return true jika admin, false jika user biasa
     */
    public boolean isAdmin() { return "ADMIN".equals(role); }

    /**
     * Mengkonversi data User ke format string untuk disimpan di file.
     * Format: email|password|username|role|gender|isPremium
     *
     * @return representasi string dari User
     */
    public String toFileLine() {
        return String.join("|", email, password, username, role,
                gender.isEmpty() ? "N/A" : gender, String.valueOf(isPremium));
    }

    /**
     * Membuat objek User dari string yang dibaca dari file.
     * Format: email|password|username|role|gender|isPremium
     *
     * @param line string yang berisi data user
     * @return objek User baru, atau null jika format tidak valid
     */
    public static User fromFileLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 4) {
            String email = parts[0];
            String password = parts[1];
            String username = parts[2];
            String role = parts[3];
            String gender = parts.length > 4 ? parts[4] : "";
            boolean premium = parts.length > 5 && Boolean.parseBoolean(parts[5]);
            return new User(email, password, username, role, gender, premium);
        }
        return null;
    }

    /**
     * Mengkonversi data User ke array Object untuk ditampilkan di tabel.
     *
     * @return array berisi email, username, role, dan status akun
     */
    public Object[] toTableRow() {
        return new Object[]{email, username, role, isPremium ? "Premium" : "Free"};
    }
}
