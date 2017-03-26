package vinhtv.android.app.popularmovies.utilities;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL-INSPIRON on 3/26/2017.
 */

public class FetchMovieStuffTask extends AsyncTaskLoader<FetchMovieStuffTask.MovieStuff> {

    private String movieId;

    public FetchMovieStuffTask(Context context, String movieId) {
        super(context);
        this.movieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public MovieStuff loadInBackground() {
        URL urlMovieVideos = NetworkUtils.buildUrl(movieId, "videos");
        URL urlMovieReviews = NetworkUtils.buildUrl(movieId, "reviews");

        try {
            JSONObject jsonMovieVideosResp, jsonMovieReviewsResp;
            String strJsonResponse = NetworkUtils.getResponseFromHttpUrl(urlMovieVideos);
            jsonMovieVideosResp = new JSONObject(strJsonResponse);
            strJsonResponse = NetworkUtils.getResponseFromHttpUrl(urlMovieReviews);
            jsonMovieReviewsResp = new JSONObject(strJsonResponse);

            return new MovieStuff(getVideos(jsonMovieVideosResp), getReviews(jsonMovieReviewsResp));
        } catch (JSONException e) {
                e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Video> getVideos(JSONObject json) throws JSONException {
        List<Video> videos = new ArrayList<>();
        JSONArray results = json.getJSONArray("results");
        for (int i=0; i<results.length(); i++) {
            JSONObject jsonVideo = results.getJSONObject(i);
            videos.add(new Video(
                    jsonVideo.getString("key"),
                    jsonVideo.getString("name")
            ));
        }
        return videos;
    }

    private List<Review> getReviews(JSONObject json) throws JSONException {
        List<Review> reviews = new ArrayList<>();
        JSONArray results = json.getJSONArray("results");
        for (int i=0; i<results.length(); i++) {
            JSONObject jsonReview = results.getJSONObject(i);
            reviews.add(new Review(
                    jsonReview.getString("author"),
                    jsonReview.getString("content")
            ));
        }
        return reviews;
    }

    public static class MovieStuff {
        public final Video[] videos;
        public final Review[] reviews;

        public MovieStuff(List<Video> videos, List<Review> reviews) {
            this.videos = videos.toArray(new Video[videos.size()]);
            this.reviews = reviews.toArray(new Review[reviews.size()]);
        }
    }

    public static class Video {
        public final String key;
        public final String name;

        public Video(String key, String name) {
            this.key = key;
            this.name = name;
        }
    }

    public static class Review {
        public final String author;
        public final String content;

        public Review(String author, String content) {
            this.author = author;
            this.content = content;
        }
    }
}
