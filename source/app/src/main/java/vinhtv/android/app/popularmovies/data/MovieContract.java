package vinhtv.android.app.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Created by DELL-INSPIRON on 3/25/2017.
 */

public final class MovieContract {

    private MovieContract() {}

    /*public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_ID = "movieID";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
    }*/

    public static class FavoriteMovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favMovie";
        public static final String COLUMN_ID = "movieID";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_ADDED_TIME = "since";
    }
}
