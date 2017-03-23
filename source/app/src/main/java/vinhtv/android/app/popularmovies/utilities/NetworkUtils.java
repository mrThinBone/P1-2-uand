package vinhtv.android.app.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import vinhtv.android.app.popularmovies.BuildConfig;

/**
 * Created by DELL-INSPIRON on 3/23/2017.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String THEMOVIEDB_URL = "https://api.themoviedb.org/3";
    private static final String THEMOVIEDB_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    public static String moviedbImageUrl(String posterPath) {
        return THEMOVIEDB_IMAGE_URL + posterPath;
    }

    /**
     * an URL to fetch movie data from themoviedb
     * @param sortBy either popular or top_rated
     * @return
     */
    public static URL buildUrl(String sortBy) {
        Uri builtUri = Uri.parse(THEMOVIEDB_URL).buildUpon()
                .appendEncodedPath("movie/" + sortBy)
                .appendQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            if(urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
