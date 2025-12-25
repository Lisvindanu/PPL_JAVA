package util;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class for mapping TMDB credits JSON response.
 */
public class TMDBCreditsResponse {
    @SerializedName("crew")
    private List<Crew> crew;

    public List<Crew> getCrew() { return crew; }

    public static class Crew {
        @SerializedName("name")
        private String name;

        @SerializedName("job")
        private String job;

        public String getName() { return name; }
        public String getJob() { return job; }
    }
}
