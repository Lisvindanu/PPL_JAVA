package model;

/**
 * Kelas model yang merepresentasikan data film dalam sistem.
 * Menyimpan informasi film termasuk metadata dan status visibility.
 * Mendukung serialisasi dan deserialisasi untuk penyimpanan file.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class Film {
    private String id; // TMDB ID
    private String title;
    private String director;
    private String genre;
    private int year;
    private String synopsis;
    private String posterPath; // TMDB poster path
    private boolean isVisible;

    /**
     * Konstruktor untuk membuat Film dengan visibility default (visible).
     *
     * @param id ID film dari TMDB
     * @param title judul film
     * @param director nama sutradara
     * @param genre genre film
     * @param year tahun rilis
     * @param synopsis sinopsis film
     * @param posterPath path poster film dari TMDB
     */
    public Film(String id, String title, String director, String genre, int year, String synopsis, String posterPath) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.year = year;
        this.synopsis = synopsis;
        this.posterPath = posterPath;
        this.isVisible = true; // Default visible
    }

    /**
     * Konstruktor lengkap untuk membuat Film dengan semua informasi termasuk visibility.
     *
     * @param id ID film dari TMDB
     * @param title judul film
     * @param director nama sutradara
     * @param genre genre film
     * @param year tahun rilis
     * @param synopsis sinopsis film
     * @param posterPath path poster film dari TMDB
     * @param isVisible status visibility untuk user biasa
     */
    public Film(String id, String title, String director, String genre, int year, String synopsis, String posterPath, boolean isVisible) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.year = year;
        this.synopsis = synopsis;
        this.posterPath = posterPath;
        this.isVisible = isVisible;
    }

    /**
     * Mendapatkan ID film.
     *
     * @return ID film dari TMDB
     */
    public String getId() { return id; }

    /**
     * Mengatur ID film.
     *
     * @param id ID film baru
     */
    public void setId(String id) { this.id = id; }

    /**
     * Mendapatkan judul film.
     *
     * @return judul film
     */
    public String getTitle() { return title; }

    /**
     * Mengatur judul film.
     *
     * @param title judul baru
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Mendapatkan nama sutradara.
     *
     * @return nama sutradara
     */
    public String getDirector() { return director; }

    /**
     * Mengatur nama sutradara.
     *
     * @param director nama sutradara baru
     */
    public void setDirector(String director) { this.director = director; }

    /**
     * Mendapatkan genre film.
     *
     * @return genre film
     */
    public String getGenre() { return genre; }

    /**
     * Mengatur genre film.
     *
     * @param genre genre baru
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * Mendapatkan tahun rilis film.
     *
     * @return tahun rilis
     */
    public int getYear() { return year; }

    /**
     * Mengatur tahun rilis film.
     *
     * @param year tahun rilis baru
     */
    public void setYear(int year) { this.year = year; }

    /**
     * Mendapatkan sinopsis film.
     *
     * @return sinopsis film
     */
    public String getSynopsis() { return synopsis; }

    /**
     * Mengatur sinopsis film.
     *
     * @param synopsis sinopsis baru
     */
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    /**
     * Mendapatkan path poster film.
     *
     * @return path poster film dari TMDB
     */
    public String getPosterPath() { return posterPath; }

    /**
     * Mengatur path poster film.
     *
     * @param posterPath path poster baru
     */
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    /**
     * Mengecek apakah film visible untuk user biasa.
     *
     * @return true jika visible, false jika hidden
     */
    public boolean isVisible() { return isVisible; }

    /**
     * Mengatur status visibility film.
     *
     * @param visible status visibility baru
     */
    public void setVisible(boolean visible) { isVisible = visible; }

    /**
     * Mengkonversi data Film ke format string untuk disimpan di file.
     * Format: id|title|director|genre|year|synopsis|posterPath|isVisible
     * Karakter pipe (|) dalam synopsis diganti dengan tilde (~).
     *
     * @return representasi string dari Film
     */
    public String toFileLine() {
        String safePosterPath = (posterPath != null) ? posterPath : "";
        return String.join("|", id, title, director, genre, String.valueOf(year),
                          synopsis.replace("|", "~"), safePosterPath, String.valueOf(isVisible));
    }

    /**
     * Membuat objek Film dari string yang dibaca dari file.
     * Format: id|title|director|genre|year|synopsis|posterPath|isVisible
     * Karakter tilde (~) dalam synopsis dikembalikan ke pipe (|).
     *
     * @param line string yang berisi data film
     * @return objek Film baru, atau null jika format tidak valid
     */
    public static Film fromFileLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length >= 6) {
            String id = parts[0];
            String title = parts[1];
            String director = parts[2];
            String genre = parts[3];
            int year = Integer.parseInt(parts[4]);
            String synopsis = parts[5].replace("~", "|");
            String posterPath = parts.length >= 7 ? parts[6] : "";
            boolean isVisible = parts.length >= 8 ? Boolean.parseBoolean(parts[7]) : true;
            return new Film(id, title, director, genre, year, synopsis, posterPath, isVisible);
        }
        return null;
    }

    /**
     * Mengkonversi data Film ke array Object untuk ditampilkan di tabel.
     *
     * @return array berisi id, title, director, genre, year, synopsis, dan status visibility
     */
    public Object[] toTableRow() {
        return new Object[]{id, title, director, genre, year, synopsis, isVisible ? "Visible" : "Hidden"};
    }
}
