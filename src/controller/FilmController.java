package controller;

import model.Film;
import util.FileManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller untuk mengelola operasi CRUD data Film.
 * Menangani pembacaan dan penulisan data film ke file storage.
 * Menyediakan method untuk manipulasi data film seperti add, delete, search, dan query berdasarkan ID.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class FilmController {

    /**
     * Konstruktor FilmController.
     * Data film dimuat dari file saat dibutuhkan (lazy loading).
     */
    public FilmController() {
        // Data loaded from file when needed
    }

    /**
     * Memuat semua data film dari file storage.
     *
     * @return list semua film yang ada dalam sistem
     */
    private List<Film> loadFilms() {
        List<Film> films = new ArrayList<>();
        List<String> lines = FileManager.readLines(FileManager.FILMS_FILE);
        for (String line : lines) {
            Film film = Film.fromFileLine(line);
            if (film != null) {
                films.add(film);
            }
        }
        return films;
    }

    /**
     * Menyimpan semua data film ke file storage.
     *
     * @param films list film yang akan disimpan
     */
    private void saveFilms(List<Film> films) {
        List<String> lines = new ArrayList<>();
        for (Film film : films) {
            lines.add(film.toFileLine());
        }
        FileManager.writeLines(FileManager.FILMS_FILE, lines);
    }

    /**
     * Menambahkan film baru ke dalam sistem.
     * Tidak akan menambahkan film jika ID sudah ada dalam database.
     *
     * @param film objek Film yang akan ditambahkan
     */
    public void addFilm(Film film) {
        List<Film> films = loadFilms();

        // Check if film with same ID already exists
        boolean exists = films.stream().anyMatch(f -> f.getId().equals(film.getId()));
        if (!exists) {
            films.add(film);
            saveFilms(films);
        }
    }

    /**
     * Menghapus film pada index tertentu.
     *
     * @param index posisi film yang akan dihapus (0-based)
     */
    public void deleteFilm(int index) {
        List<Film> films = loadFilms();
        if (index >= 0 && index < films.size()) {
            films.remove(index);
            saveFilms(films);
        }
    }

    /**
     * Mendapatkan film pada index tertentu.
     *
     * @param index posisi film dalam list (0-based)
     * @return objek Film jika ditemukan, null jika index tidak valid
     */
    public Film getFilm(int index) {
        List<Film> films = loadFilms();
        if (index >= 0 && index < films.size()) {
            return films.get(index);
        }
        return null;
    }

    /**
     * Mendapatkan film berdasarkan ID (TMDB ID).
     *
     * @param id ID film yang dicari
     * @return objek Film jika ditemukan, null jika tidak ada
     */
    public Film getFilmById(String id) {
        List<Film> films = loadFilms();
        return films.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Mendapatkan semua film yang ada dalam sistem.
     *
     * @return list semua film
     */
    public List<Film> getAllFilms() {
        return loadFilms();
    }

    /**
     * Mencari film berdasarkan judul (case-insensitive, partial match).
     *
     * @param title judul atau bagian dari judul yang dicari
     * @return list film yang judulnya mengandung keyword pencarian
     */
    public List<Film> searchByTitle(String title) {
        List<Film> films = loadFilms();
        List<Film> results = new ArrayList<>();
        for (Film film : films) {
            if (film.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(film);
            }
        }
        return results;
    }

    /**
     * Menghitung total jumlah film dalam sistem.
     *
     * @return jumlah total film
     */
    public int getFilmCount() {
        return loadFilms().size();
    }
}
