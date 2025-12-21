package model;

import java.util.Arrays;
import java.util.List;

/**
 * Kelas model yang merepresentasikan playlist film dalam sistem.
 * Setiap playlist dimiliki oleh satu user dan berisi daftar ID film.
 * Mendukung serialisasi dan deserialisasi untuk penyimpanan file.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class Playlist {
    private String name;
    private String ownerEmail;
    private String visibility;
    private List<String> filmIds;

    /**
     * Konstruktor untuk membuat Playlist baru.
     *
     * @param name nama playlist
     * @param ownerEmail email pemilik playlist
     * @param visibility status visibility playlist ("Public" atau "Private")
     * @param filmIds daftar ID film yang ada dalam playlist
     */
    public Playlist(String name, String ownerEmail, String visibility, List<String> filmIds) {
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.visibility = visibility;
        this.filmIds = filmIds;
    }

    /**
     * Mendapatkan nama playlist.
     *
     * @return nama playlist
     */
    public String getName() { return name; }

    /**
     * Mengatur nama playlist.
     *
     * @param name nama baru
     */
    public void setName(String name) { this.name = name; }

    /**
     * Mendapatkan email pemilik playlist.
     *
     * @return email pemilik
     */
    public String getOwnerEmail() { return ownerEmail; }

    /**
     * Mengatur email pemilik playlist.
     *
     * @param ownerEmail email pemilik baru
     */
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    /**
     * Mendapatkan status visibility playlist.
     *
     * @return visibility ("Public" atau "Private")
     */
    public String getVisibility() { return visibility; }

    /**
     * Mengatur status visibility playlist.
     *
     * @param visibility visibility baru
     */
    public void setVisibility(String visibility) { this.visibility = visibility; }

    /**
     * Mendapatkan daftar ID film dalam playlist.
     *
     * @return list berisi ID film
     */
    public List<String> getFilmIds() { return filmIds; }

    /**
     * Mengatur daftar ID film dalam playlist.
     *
     * @param filmIds list ID film baru
     */
    public void setFilmIds(List<String> filmIds) { this.filmIds = filmIds; }

    /**
     * Mengkonversi data Playlist ke format string untuk disimpan di file.
     * Format: name|ownerEmail|visibility|filmId1,filmId2,filmId3
     *
     * @return representasi string dari Playlist
     */
    public String toFileLine() {
        String filmIdsStr = String.join(",", filmIds);
        return String.join("|", name, ownerEmail, visibility, filmIdsStr);
    }

    /**
     * Membuat objek Playlist dari string yang dibaca dari file.
     * Format: name|ownerEmail|visibility|filmId1,filmId2,filmId3
     *
     * @param line string yang berisi data playlist
     * @return objek Playlist baru, atau null jika format tidak valid
     */
    public static Playlist fromFileLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 3) {
            String name = parts[0];
            String ownerEmail = parts[1];
            String visibility = parts[2];
            List<String> filmIds = new java.util.ArrayList<>();
            if (parts.length >= 4 && !parts[3].isEmpty()) {
                filmIds = new java.util.ArrayList<>(Arrays.asList(parts[3].split(",")));
            }
            return new Playlist(name, ownerEmail, visibility, filmIds);
        }
        return null;
    }

    /**
     * Mengkonversi data Playlist ke array Object untuk ditampilkan di tabel.
     *
     * @return array berisi name, ownerEmail, visibility, jumlah film, dan daftar ID film
     */
    public Object[] toTableRow() {
        return new Object[]{name, ownerEmail, visibility, filmIds.size(), String.join(", ", filmIds)};
    }
}
