package vinhtv.android.app.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.utilities.NetworkUtils;

import static vinhtv.android.app.popularmovies.R.id.detail_movie_releaseDate;

public class MovieDetailActivity extends AppCompatActivity {

    static final String EXTRA_DATA = "ext_data";

    TextView tvMovieTitle;
    ImageView ivMoviePoster;
    TextView tvMovieRating;
    TextView tvMovieReleaseDate;
    TextView tvMovieOverview;
    TextView tvMovieDuration;

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

        Movie movie = getIntent().getParcelableExtra(EXTRA_DATA);
        tvMovieTitle.setText(movie.title());
        tvMovieReleaseDate.setText(movie.releaseDate());
        tvMovieRating.setText(getString(R.string.movie_rating, movie.rating()));
        tvMovieOverview.setText(movie.overview());
        tvMovieDuration.setText("#");
        Picasso.with(getApplicationContext()).load(
                NetworkUtils.moviedbImageUrl(movie.posterPath())
        ).fit().into(ivMoviePoster);
    }
}
