package vinhtv.android.app.popularmovies.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DELL-INSPIRON on 3/23/2017.
 */

public class Movie {
    private final long id;
    private final String posterPath;
    private final String backdropPath;
    private final String title;
    private final double voting;
    private final String releaseDate;

    public Movie(JSONObject jsonMovie) throws JSONException {
        id = jsonMovie.getLong("id");
        posterPath = jsonMovie.getString("poster_path");
        backdropPath = jsonMovie.has("backdrop_path") ? jsonMovie.getString("backdrop_path") : null;
        title = jsonMovie.getString("original_title");
        voting = jsonMovie.has("vote_average") ? jsonMovie.getDouble("vote_average") : 0f;
        releaseDate = jsonMovie.getString("release_date");
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public double getVoting() {
        return voting;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return id+"#"+title;
    }
}
