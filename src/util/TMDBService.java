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
    // TMDB API Key - https://www.themoviedb.org/settings/api
    private static final String API_KEY = ConfigManager.getProperty("tmdb.api.key");
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
    private static final com.google.gson.Gson gson = new com.google.gson.Gson();

    /**
     * Mengambil data film dari TMDB API berdasarkan movie ID.
     * Melakukan dua API call: movie details dan credits.
     *
     * @param movieId ID film di TMDB
     * @return objek MovieData berisi informasi film
     * @throws Exception jika terjadi error saat API call atau parsing
     */
    public static MovieData fetchMovieData(String movieId) throws Exception {
        // Fetch movie details
        String movieUrl = BASE_URL + movieId + "?api_key=" + API_KEY;
        String movieResponseJson = getStringFromURL(movieUrl);
        TMDBMovieResponse movieResp = gson.fromJson(movieResponseJson, TMDBMovieResponse.class);

        String title = movieResp.getTitle();
        String synopsis = movieResp.getOverview();
        String releaseDate = movieResp.getReleaseDate();
        int year = (releaseDate == null || releaseDate.isEmpty()) ? 2024 : Integer.parseInt(releaseDate.split("-")[0]);

        // Get first genre
        String genre = "Unknown";
        if (movieResp.getGenres() != null && !movieResp.getGenres().isEmpty()) {
            genre = movieResp.getGenres().get(0).getName();
        }

        // Fetch credits for director
        String creditsUrl = BASE_URL + movieId + "/credits?api_key=" + API_KEY;
        String creditsResponseJson = getStringFromURL(creditsUrl);
        TMDBCreditsResponse creditsResp = gson.fromJson(creditsResponseJson, TMDBCreditsResponse.class);

        String director = "Unknown";
        if (creditsResp.getCrew() != null) {
            for (TMDBCreditsResponse.Crew crewMember : creditsResp.getCrew()) {
                if ("Director".equals(crewMember.getJob())) {
                    director = crewMember.getName();
                    break;
                }
            }
        }

        return new MovieData(title, director, genre, year, synopsis);
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

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed: HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        conn.disconnect();

        return response.toString();
    }
}
