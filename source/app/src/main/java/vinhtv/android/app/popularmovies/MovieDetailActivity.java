package vinhtv.android.app.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.utilities.AddFavoriteTask;
import vinhtv.android.app.popularmovies.utilities.AddFavoriteTask.TaskResult;
import vinhtv.android.app.popularmovies.utilities.NetworkUtils;

import static vinhtv.android.app.popularmovies.R.id.detail_movie_releaseDate;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<TaskResult>,
        View.OnClickListener {

    private static final int LOADER_ID = 1001;
    static final String EXTRA_DATA = "ext_data";

    TextView tvMovieTitle;
    ImageView ivMoviePoster;
    TextView tvMovieRating;
    TextView tvMovieReleaseDate;
    TextView tvMovieOverview;
    TextView tvMovieDuration;
    Button btnAddFavorite;

    Toast mResultMsg;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        tvMovieTitle = (TextView) findViewById(R.id.detail_movie_title);
        ivMoviePoster = (ImageView) findViewById(R.id.detail_movie_poster);
        tvMovieRating = (TextView) findViewById(R.id.detail_movie_rating);
        tvMovieReleaseDate = (TextView) findViewById(detail_movie_releaseDate);
        tvMovieOverview = (TextView) findViewById(R.id.detail_movie_overview);
        tvMovieDuration = (TextView) findViewById(R.id.detail_movie_duration);
        btnAddFavorite = (Button) findViewById(R.id.btn_add_favorite);

        Movie movie = getIntent().getParcelableExtra(EXTRA_DATA);
        tvMovieTitle.setText(movie.title());
        tvMovieReleaseDate.setText(movie.releaseDate());
        tvMovieRating.setText(getString(R.string.movie_rating, movie.rating()));
        tvMovieOverview.setText(movie.overview());
        tvMovieDuration.setText("#");
        Picasso.with(getApplicationContext()).load(
                NetworkUtils.moviedbImageUrl(movie.posterPath())
        ).fit().into(ivMoviePoster);
        mMovie = movie;
    }

    @Override
    public void onClick(View view) {
        Bundle args = new Bundle();
        args.putParcelable(AddFavoriteTask.ARG_DATA, mMovie);
        getSupportLoaderManager().restartLoader(LOADER_ID, args, this);
    }

    @Override
    public Loader<TaskResult> onCreateLoader(int id, Bundle args) {
        return new AddFavoriteTask(this, args);
    }

    @Override
    public void onLoadFinished(Loader<AddFavoriteTask.TaskResult> loader, TaskResult data) {
        if(data == null) return;

        if(mResultMsg != null) mResultMsg.cancel();

        if(!data.success) {
            mResultMsg = Toast.makeText(this, getString(R.string.add_favorite_msg_failed), Toast.LENGTH_SHORT);
            return;
        }
        String operation = data.cmd == TaskResult.CMD.ADD ? "added" : "removed";
        mResultMsg = Toast.makeText(this, getString(R.string.add_favorite_msg_success, operation), Toast.LENGTH_SHORT);
        mResultMsg.show();
    }

    @Override
    public void onLoaderReset(Loader<AddFavoriteTask.TaskResult> loader) {}
}
