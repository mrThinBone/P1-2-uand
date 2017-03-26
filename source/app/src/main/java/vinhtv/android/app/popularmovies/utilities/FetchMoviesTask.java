package vinhtv.android.app.popularmovies.utilities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.data.MovieContract;

/**
 * Created by DELL-INSPIRON on 3/25/2017.
 */

public class FetchMoviesTask extends AsyncTaskLoader<List<Movie>> {

    public static final String SEARCH_QUERY_SORTBY_EXTRA = "sortBy";

    private Bundle mArguments;

    public FetchMoviesTask(Context context, Bundle args) {
        super(context);
        mArguments = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        String sortBy = mArguments.getString(SEARCH_QUERY_SORTBY_EXTRA, "popular");
        List<Movie> result = new ArrayList<>();
        if(sortBy.equals("favorite")) {
            // get favorite movies from local db
            Cursor cursor = getContext().getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                    null, null, null,
                    MovieContract.FavoriteMovieEntry.COLUMN_ADDED_TIME + " DESC");

            if(cursor != null) result.addAll(Utilities.moviesCursorToList(cursor));
        } else {
            // get movies from TheMovieDB api
            URL url = NetworkUtils.buildUrl(sortBy);
            try {
                String jsonResponseString = NetworkUtils.getResponseFromHttpUrl(url);
                if (jsonResponseString == null) return null;

                JSONObject jsonResponse = new JSONObject(jsonResponseString);
                if (jsonResponse.has("results")) {
                    JSONArray jsonMovies = jsonResponse.getJSONArray("results");
                    for (int i = 0; i < jsonMovies.length(); i++) {
                        JSONObject jsonMovie = jsonMovies.getJSONObject(i);
                        result.add(new Movie(jsonMovie));
                    }
                    return result;
                }
            } catch (JSONException e) {
                Log.e(e.getClass().getName(), e.getMessage(), e);
            } catch (IOException e) {
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
        return result;
    }
}
