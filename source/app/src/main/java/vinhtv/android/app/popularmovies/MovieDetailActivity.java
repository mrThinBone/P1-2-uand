package vinhtv.android.app.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.utilities.AddFavoriteTask;
import vinhtv.android.app.popularmovies.utilities.AddFavoriteTask.TaskResult;
import vinhtv.android.app.popularmovies.utilities.FetchMovieStuffTask;
import vinhtv.android.app.popularmovies.utilities.FetchMovieStuffTask.MovieStuff;
import vinhtv.android.app.popularmovies.utilities.NetworkUtils;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<TaskResult>,
        View.OnClickListener {

    private static final int ADD_REMOVE_FAVORITE_LOADER_ID = 1001;
    private static final int MOVIE_STUFF_LOADER_ID = 1002;
    static final String EXTRA_DATA = "ext_data";

    @BindView(R.id.detail_movie_title) TextView tvMovieTitle;
    @BindView(R.id.detail_movie_poster) ImageView ivMoviePoster;
    @BindView(R.id.detail_movie_rating) TextView tvMovieRating;
    @BindView(R.id.detail_movie_releaseDate) TextView tvMovieReleaseDate;
    @BindView(R.id.detail_movie_overview) TextView tvMovieOverview;
    @BindView(R.id.detail_movie_duration) TextView tvMovieDuration;
    @BindView(R.id.ll_movie_reviews) LinearLayout mReviewsContainer;
    @BindView(R.id.ll_movie_videos) LinearLayout mVideosContainer;

    Toast mResultMsg;
    private Movie mMovie;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

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

        Bundle args = new Bundle();
        args.putString("movie_id", String.valueOf(movie.id()));
        getSupportLoaderManager().initLoader(MOVIE_STUFF_LOADER_ID, args, movieStuffLoaderCallbacks);
    }

    @OnClick(R.id.btn_add_favorite)
    public void addToFavoriteCollection() {
        Bundle args = new Bundle();
        args.putParcelable(AddFavoriteTask.ARG_DATA, mMovie);
        getSupportLoaderManager().restartLoader(ADD_REMOVE_FAVORITE_LOADER_ID, args, this);
    }

    @Override
    public void onClick(View view) {
        String key = (String) view.getTag();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));

        // Check if the youtube app exists on the device
        if (intent.resolveActivity(getPackageManager()) == null) {
            // If the youtube app doesn't exist, then use the browser
            intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + key));
        }

        startActivity(intent);
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

    private LoaderManager.LoaderCallbacks<MovieStuff> movieStuffLoaderCallbacks = new LoaderManager.LoaderCallbacks<MovieStuff>() {
        @Override
        public Loader<MovieStuff> onCreateLoader(int id, Bundle args) {
            String movieId = args.getString("movie_id");
            return new FetchMovieStuffTask(MovieDetailActivity.this, movieId);
        }

        @Override
        public void onLoadFinished(Loader<MovieStuff> loader, MovieStuff data) {
            if(data == null || loaded) return;

            LayoutInflater layoutInflater = LayoutInflater.from(MovieDetailActivity.this);
            if(data.videos.length > 0) {
                mVideosContainer.setVisibility(View.VISIBLE);
                int length = data.videos.length;
                for (int i=0; i<length; i++) {
                    FetchMovieStuffTask.Video video = data.videos[i];

                    ViewGroup videoLayout = (ViewGroup) layoutInflater.inflate(R.layout.item_movie_video, null, false);
                    TextView tvVideoTitle = (TextView) videoLayout.findViewById(R.id.tv_trailer_title);
                    ImageButton btnPlay = (ImageButton) videoLayout.findViewById(R.id.btn_trailer_play);
                    tvVideoTitle.setText(video.name);
                    btnPlay.setTag(video.key);
                    btnPlay.setOnClickListener(MovieDetailActivity.this);

                    mVideosContainer.addView(videoLayout);
                    if(i != length-1) {
                        View separator = layoutInflater.inflate(R.layout.view_separator, null, false);
                        mVideosContainer.addView(separator);
                    }
                }
            }

            if(data.reviews.length > 0) {
                Resources resources = getResources();
                mReviewsContainer.setVisibility(View.VISIBLE);
                int length = data.reviews.length;
                for (int i=0; i<length; i++) {
                    FetchMovieStuffTask.Review review = data.reviews[i];

                    ViewGroup reviewLayout = (ViewGroup) layoutInflater.inflate(R.layout.item_movie_review, null, false);
                    TextView tvAuthor = (TextView) reviewLayout.findViewById(R.id.tv_review_author);
                    TextView tvContent = (TextView) reviewLayout.findViewById(R.id.tv_review_content);
                    tvAuthor.setText(review.author);
                    tvContent.setText(review.content);
                    if(i % 2 != 0) {
                        reviewLayout.setBackgroundColor(resources.getColor(R.color.colorLowAccent));
                    }

                    mReviewsContainer.addView(reviewLayout);
                }
            }
            loaded = true;
        }

        @Override
        public void onLoaderReset(Loader<MovieStuff> loader) {}
    };
}
