package vinhtv.android.app.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    MovieAdapter mAdapter;
    FetchWeatherTask fetchWeatherTask;

    private String mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rclView = (RecyclerView) findViewById(R.id.rcl_movies);
        rclView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MovieAdapter(this, null);
        rclView.setAdapter(mAdapter);

        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey("m_sort_by"))
                mSortBy = savedInstanceState.getString("m_sort_by");
            else
                mSortBy = getSortByFromPref();

            if(savedInstanceState.containsKey("m_data")) {
                mAdapter.swap(savedInstanceState.<Movie>getParcelableArrayList("m_data"));
            } else {
                fetchWeatherTask = new FetchWeatherTask(this);
                fetchWeatherTask.execute(mSortBy);
            }
        }
        mAdapter.setListItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = getSortByFromPref();
        if(!sortBy.equals(mSortBy)) {
            mSortBy = sortBy;
            fetchWeatherTask = new FetchWeatherTask(this);
            fetchWeatherTask.execute(mSortBy);
        }
    }

    private String getSortByFromPref() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.pref_sortby_key), getString(R.string.pref_sortby_default_value));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("m_sort_by", mSortBy);
        outState.putParcelableArrayList("m_data", (ArrayList<Movie>) mAdapter.getData());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent openDetail = new Intent(this, MovieDetailActivity.class);
        openDetail.putExtra(MovieDetailActivity.EXTRA_DATA, movie);
        startActivity(openDetail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(fetchWeatherTask != null && !fetchWeatherTask.isCancelled())
            fetchWeatherTask.cancel(true);
        super.onDestroy();
    }

    static class FetchWeatherTask extends AsyncTask<String, Void, List<Movie>> {

        private final WeakReference<MainActivity> mActivityPref;

        public FetchWeatherTask(MainActivity activity) {
            mActivityPref = new WeakReference<>(activity);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            String sortBy = params[0];
            URL url = NetworkUtils.buildUrl(sortBy);
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

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            MainActivity activity = mActivityPref.get();
            if(movies != null && activity != null) {
                activity.mAdapter.swap(movies);
            }
        }
    }
}
