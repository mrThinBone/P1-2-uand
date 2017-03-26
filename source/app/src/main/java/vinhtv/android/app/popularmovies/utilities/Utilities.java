package vinhtv.android.app.popularmovies.utilities;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import vinhtv.android.app.popularmovies.data.Movie;

/**
 * Created by DELL-INSPIRON on 3/26/2017.
 */

public class Utilities {

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_POSTER = 1;
    private static final int COL_MOVIE_TITLE = 2;
    private static final int COL_MOVIE_RELEASE_DATE = 3;
    private static final int COL_MOVIE_OVERVIEW = 4;
    private static final int COL_MOVIE_RATING = 5;

    public static List<Movie> moviesCursorToList(Cursor cursor) {
        List<Movie> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            result.add(new Movie(
                    cursor.getLong(COL_MOVIE_ID),
                    cursor.getString(COL_MOVIE_POSTER),
                    cursor.getString(COL_MOVIE_TITLE),
                    cursor.getFloat(COL_MOVIE_RATING),
                    cursor.getString(COL_MOVIE_RELEASE_DATE),
                    cursor.getString(COL_MOVIE_OVERVIEW)
            ));
        }
        return result;
    }
}
