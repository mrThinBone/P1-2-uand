package vinhtv.android.app.popularmovies.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Date;

import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.data.MovieContract;

/**
 * Created by DELL-INSPIRON on 3/26/2017.
 */

public class AddFavoriteTask extends AsyncTaskLoader<AddFavoriteTask.TaskResult> {

    public static final String ARG_DATA = "_data";

    private Bundle mArgs;

    public AddFavoriteTask(Context context, Bundle args) {
        super(context);
        mArgs = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public TaskResult loadInBackground() {
        Movie movie = mArgs.getParcelable(ARG_DATA);
        if(movie == null) return null;

        Uri queryUri = ContentUris.withAppendedId(MovieContract.FavoriteMovieEntry.CONTENT_URI, movie.id());
        ContentResolver cr = getContext().getContentResolver();
        Cursor queryResult = cr.query(queryUri, new String[] {MovieContract.FavoriteMovieEntry._ID}, null, null, null, null);
        if(queryResult!= null && queryResult.moveToFirst()) {
            // remove
            return new TaskResult(TaskResult.CMD.REMOVE, cr.delete(queryUri, null, null) > 0);
        } else {
            // add
            ContentValues values = new ContentValues();
            values.put(MovieContract.FavoriteMovieEntry._ID, movie.id());
            values.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER, movie.posterPath());
            values.put(MovieContract.FavoriteMovieEntry.COLUMN_TITLE, movie.title());
            values.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate());
            values.put(MovieContract.FavoriteMovieEntry.COLUMN_RATING, movie.rating());
            values.put(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, movie.overview());
            values.put(MovieContract.FavoriteMovieEntry.COLUMN_ADDED_TIME, new Date().getTime());
            Uri uri = getContext().getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI, values);
            String id = String.valueOf(movie.id());
            return new TaskResult(TaskResult.CMD.ADD, uri != null && uri.getLastPathSegment().equals(id));
        }
    }

    public static class TaskResult {
        public enum CMD {ADD, REMOVE}
        public final CMD cmd;
        public final boolean success;

        private TaskResult(CMD cmd, boolean success) {
            this.cmd = cmd;
            this.success = success;
        }
    }
}