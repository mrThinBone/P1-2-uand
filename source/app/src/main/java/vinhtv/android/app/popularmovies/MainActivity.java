package vinhtv.android.app.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    TextView dummyTextView;
    FetchWeatherTask fetchWeatherTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dummyTextView = (TextView) findViewById(R.id.tv_dummy);
        fetchWeatherTask = new FetchWeatherTask(this);
        fetchWeatherTask.execute("popular");
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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            MainActivity activity = mActivityPref.get();
            if(movies != null && activity != null) {
                StringBuilder strBuilder = new StringBuilder();
                for (Movie movie: movies) {
                    strBuilder.append(movie).append("\n");
                }
                activity.dummyTextView.setText(strBuilder.toString());
            }
        }
    }
}
