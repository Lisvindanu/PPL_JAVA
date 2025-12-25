package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Service untuk mengambil data film dari The Movie Database (TMDB) API.
 * Menangani HTTP request ke TMDB API dan parsing response JSON.
 * Menyediakan method untuk fetch movie details dan credits berdasarkan movie ID.
 *
 * @author lisvindanu
 * @version 2.0
 */
public class TMDBService {
    // TMDB API Key - loaded from config.properties
    private static final String API_KEY = ConfigManager.get("tmdb.api.key");
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    /**
     * Inner class untuk menyimpan data film yang diambil dari TMDB.
     * Berisi informasi dasar film seperti title, director, genre, year, dan synopsis.
     */
    public static class MovieData {
        public String title;
        public String director;
        public String genre;
        public int year;
        public String synopsis;

        /**
         * Konstruktor untuk membuat objek MovieData.
         *
         * @param title judul film
         * @param director nama sutradara
         * @param genre genre film
         * @param year tahun rilis
         * @param synopsis sinopsis film
         */
        public MovieData(String title, String director, String genre, int year, String synopsis) {
            this.title = title;
            this.director = director;
            this.genre = genre;
            this.year = year;
            this.synopsis = synopsis;
        }
    }

    /**
     * Mengambil data film dari TMDB API berdasarkan movie ID.
     * Melakukan dua API call: movie details dan credits.
     *
     * @param movieId ID film di TMDB
     * @return objek MovieData berisi informasi film
     * @throws Exception jika terjadi error saat API call atau parsing
     */
    public static MovieData fetchMovieData(String movieId) throws Exception {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new RuntimeException("TMDB API Key not found in config.properties");
        }

        // Fetch movie details
        String movieUrl = BASE_URL + movieId + "?api_key=" + API_KEY;
        String movieResponse = getStringFromURL(movieUrl);

        String title = extractValue(movieResponse, "\"title\":");
        String synopsis = extractValue(movieResponse, "\"overview\":");
        String releaseDate = extractValue(movieResponse, "\"release_date\":");
        int year = releaseDate.isEmpty() ? 2024 : Integer.parseInt(releaseDate.split("-")[0]);

        // Get first genre
        String genre = extractFirstGenre(movieResponse);

        // Fetch credits for director
        String creditsUrl = BASE_URL + movieId + "/credits?api_key=" + API_KEY;
        String creditsResponse = getStringFromURL(creditsUrl);
        String director = extractDirector(creditsResponse);

        return new MovieData(title, director, genre, year, synopsis);
    }

    /**
     * Mengekstrak nilai dari JSON response berdasarkan key.
     * Menggunakan simple string parsing (bukan JSON parser library).
     *
     * @param json string JSON response
     * @param key key yang dicari (contoh: "title":)
     * @return nilai dari key tersebut, atau empty string jika tidak ditemukan
     */
    private static String extractValue(String json, String key) {
        int startIndex = json.indexOf(key);
        if (startIndex == -1) return "";

        startIndex = json.indexOf("\"", startIndex + key.length()) + 1;
        int endIndex = json.indexOf("\"", startIndex);

        if (startIndex == -1 || endIndex == -1) return "";
        return json.substring(startIndex, endIndex);
    }

    /**
     * Mengekstrak genre pertama dari array genres dalam JSON response.
     *
     * @param json string JSON response
     * @return nama genre pertama, atau "Unknown" jika tidak ada
     */
    private static String extractFirstGenre(String json) {
        int genresIndex = json.indexOf("\"genres\":");
        if (genresIndex == -1) return "Unknown";

        int nameIndex = json.indexOf("\"name\":", genresIndex);
        if (nameIndex == -1) return "Unknown";

        int startQuote = json.indexOf("\"", nameIndex + 7) + 1;
        int endQuote = json.indexOf("\"", startQuote);

        if (startQuote == -1 || endQuote == -1) return "Unknown";
        return json.substring(startQuote, endQuote);
    }

    /**
     * Mengekstrak nama director dari credits JSON response.
     * Mencari crew member dengan job "Director".
     *
     * @param json string JSON credits response
     * @return nama director, atau "Unknown" jika tidak ditemukan
     */
    private static String extractDirector(String json) {
        String crew = json.substring(json.indexOf("\"crew\":"));

        int jobIndex = crew.indexOf("\"job\":\"Director\"");
        if (jobIndex == -1) return "Unknown";

        String beforeJob = crew.substring(0, jobIndex);
        int lastNameIndex = beforeJob.lastIndexOf("\"name\":\"");
        if (lastNameIndex == -1) return "Unknown";

        int startQuote = lastNameIndex + 8;
        int endQuote = beforeJob.indexOf("\"", startQuote);

        if (endQuote == -1) {
            endQuote = crew.indexOf("\"", jobIndex - (beforeJob.length() - startQuote));
        }

        return beforeJob.substring(startQuote, endQuote);
    }

    /**
     * Melakukan HTTP GET request ke URL dan mengembalikan response sebagai string.
     *
     * @param urlString URL tujuan
     * @return response body sebagai string
     * @throws Exception jika HTTP status bukan 200 atau terjadi error I/O
     */
    private static String getStringFromURL(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        try {
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed: HTTP error code : " + conn.getResponseCode());
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
