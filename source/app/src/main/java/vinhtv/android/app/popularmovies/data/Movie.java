package vinhtv.android.app.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DELL-INSPIRON on 3/23/2017.
 */

public class Movie implements Parcelable {
    private final long id;
    private final String posterPath;
    private final String backdropPath;
    private final String title;
    private final double rating;
    private final String releaseDate;
    private final String overview;

    public Movie(JSONObject jsonMovie) throws JSONException {
        id = jsonMovie.getLong("id");
        posterPath = jsonMovie.getString("poster_path");
        backdropPath = jsonMovie.has("backdrop_path") ? jsonMovie.getString("backdrop_path") : null;
        title = jsonMovie.getString("original_title");
        rating = jsonMovie.has("vote_average") ? jsonMovie.getDouble("vote_average") : 0f;
        releaseDate = jsonMovie.getString("release_date");
        overview = jsonMovie.has("overview") ? jsonMovie.getString("overview") : "";
    }

    public String posterPath() {
        return posterPath;
    }

    public String ackdropPath() {
        return backdropPath;
    }

    public String title() {
        return title;
    }

    public double rating() {
        return rating;
    }

    public String releaseDate() {
        return releaseDate;
    }

    public String overview() {
        return overview;
    }

    @Override
    public String toString() {
        return id+"#"+title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(title);
        parcel.writeDouble(rating);
        parcel.writeString(releaseDate);
        parcel.writeString(overview);
    }

    private Movie(Parcel in) {
        id = in.readLong();
        posterPath = in.readString();
        backdropPath = in.readString();
        title = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();
        overview = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
