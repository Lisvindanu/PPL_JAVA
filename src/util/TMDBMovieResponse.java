package util;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class for mapping TMDB movie details JSON response.
 */
public class TMDBMovieResponse {
    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview; // Synopsis

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("poster_path")
    private String posterPath;

    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getReleaseDate() { return releaseDate; }
    public List<Genre> getGenres() { return genres; }
    public String getPosterPath() { return posterPath; }

    public static class Genre {
        @SerializedName("name")
        private String name;

        public String getName() { return name; }
    }
}
