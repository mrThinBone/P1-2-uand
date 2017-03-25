package vinhtv.android.app.popularmovies.utilities;

import android.content.Context;
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

/**
 * Created by DELL-INSPIRON on 3/25/2017.
 */

public class FetchMoviesTask extends AsyncTaskLoader<List<Movie>> {

    public static final String SEARCH_QUERY_SORTBY_EXTRA = "sortBy";

    private String mSortBy;

    public FetchMoviesTask(Context context, Bundle args) {
        super(context);
        this.mSortBy = args.getString(SEARCH_QUERY_SORTBY_EXTRA, "popular");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        URL url = NetworkUtils.buildUrl(mSortBy);
        try {
            String jsonResponseString = NetworkUtils.getResponseFromHttpUrl(url);
            if(jsonResponseString == null) return null;

            JSONObject jsonResponse = new JSONObject(jsonResponseString);
            if(jsonResponse.has("results")) {
                List<Movie> results = new ArrayList<>();
                JSONArray jsonMovies = jsonResponse.getJSONArray("results");
                for (int i=0; i<jsonMovies.length(); i++) {
                    JSONObject jsonMovie = jsonMovies.getJSONObject(i);
                    results.add(new Movie(jsonMovie));
                }
                return results;
            } else {
                return null;
            }
        } catch (JSONException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        } catch (IOException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return null;
    }
}
