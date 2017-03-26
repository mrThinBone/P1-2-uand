package vinhtv.android.app.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by DELL-INSPIRON on 3/26/2017.
 */

public class MovieProvider extends ContentProvider {

    public static final int FAVORITE_MOVIES = 100;
    public static final int FAVORITE_MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // for directory
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES, FAVORITE_MOVIES);
        // single item
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES + "/#", FAVORITE_MOVIE_WITH_ID);

        return uriMatcher;
    }

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db =getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor retCursor;
        switch (match) {
            case FAVORITE_MOVIES:
                retCursor = db.query(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                selection = MovieContract.FavoriteMovieEntry._ID + " = ?";
                selectionArgs = new String[] {id};
                retCursor = db.query(MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // notify cursor if there're any changes on given Uri
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri = null;
        switch (match) {
            case FAVORITE_MOVIES:
                long id = db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.FavoriteMovieEntry.CONTENT_URI, id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int deletedRows;
        switch (match) {
            case FAVORITE_MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                whereClause = MovieContract.FavoriteMovieEntry._ID + " = ?";
                whereArgs = new String[] {id};
                deletedRows = db.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(MovieContract.FavoriteMovieEntry.CONTENT_URI, null);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
//        SQLiteDatabase db = getWritableDatabase();
        return 0;
    }

    private SQLiteDatabase getWritableDatabase() {
        return mMovieDbHelper.getWritableDatabase();
    }

    private SQLiteDatabase getReadableDatabase() {
        return mMovieDbHelper.getReadableDatabase();
    }
}