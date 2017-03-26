package vinhtv.android.app.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.utilities.FetchMoviesTask;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private static final int LOADER_FETCH_MOVIE_ID = 2003;

    FrameLayout progressBar;
    TextView tvNoContent;
    MovieAdapter mAdapter;
    private String mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (FrameLayout) findViewById(R.id.progress_bar);
        tvNoContent = (TextView) findViewById(R.id.tv_no_content);
        RecyclerView rclView = (RecyclerView) findViewById(R.id.rcl_movies);
        rclView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new MovieAdapter(this, null);
        rclView.setAdapter(mAdapter);
        mAdapter.setListItemClickListener(this);

        if(savedInstanceState != null && savedInstanceState.containsKey("m_sort_by")) {
            mSortBy = savedInstanceState.getString("m_sort_by");
        } else {
            mSortBy = getSortByFromPref();
        }

        getSupportLoaderManager().initLoader(LOADER_FETCH_MOVIE_ID, createQueryBundle(mSortBy), mFetchMovieDataCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = getSortByFromPref();
        if (!sortBy.equals(mSortBy)) {
            mSortBy = sortBy;
            //*NOTE: restart loader also create a new loader if not exist
            getSupportLoaderManager().restartLoader(LOADER_FETCH_MOVIE_ID, createQueryBundle(mSortBy), mFetchMovieDataCallback);
        }
    }

    /**
     * a Bundle which is serve as argument for Loader
     * perform network request to fetch movie data from TheMovieDB
     */
    private Bundle createQueryBundle(String sortBy) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(FetchMoviesTask.SEARCH_QUERY_SORTBY_EXTRA, sortBy);
        return queryBundle;
    }

    private String getSortByFromPref() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(getString(R.string.pref_sortby_key), getString(R.string.pref_sortby_default_value));
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent openDetail = new Intent(this, MovieDetailActivity.class);
        openDetail.putExtra(MovieDetailActivity.EXTRA_DATA, movie);
        startActivity(openDetail);
    }

    private LoaderManager.LoaderCallbacks mFetchMovieDataCallback = new LoaderManager.LoaderCallbacks<List<Movie>>() {

        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            progressBar.setVisibility(View.VISIBLE);
            return new FetchMoviesTask(MainActivity.this, args);
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
            if(movies != null) {
                mAdapter.swap(movies);
                tvNoContent.setVisibility(View.INVISIBLE);
            } else {
                tvNoContent.setVisibility(View.VISIBLE);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {}
    };

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("m_sort_by", mSortBy);
        super.onSaveInstanceState(outState);
    }
}
