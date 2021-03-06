package vinhtv.android.app.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import vinhtv.android.app.popularmovies.data.Movie;
import vinhtv.android.app.popularmovies.utilities.NetworkUtils;

/**
 * Created by DELL-INSPIRON on 3/23/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private List<Movie> mMovies;
    private ListItemClickListener mListener;

    public MovieAdapter(Context context, List<Movie> data) {
        mContext = context;
        this.mMovies = data;
    }

    public interface ListItemClickListener {
        void onItemClick(Movie movie);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_movie, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        Picasso.with(mContext.getApplicationContext()).load(
                NetworkUtils.moviedbImageUrl(movie.posterPath())
        ).fit().into(holder.ivPoster);
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    public List<Movie> getData() {
        return new ArrayList<>(mMovies);
    }

    void swap(List<Movie> data) {
        if(mMovies == null) {
            mMovies = new ArrayList<>(data);
        } else {
            mMovies.clear();
            mMovies.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setListItemClickListener(ListItemClickListener listener) {
        this.mListener = listener;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivPoster;

        MovieViewHolder(View itemView) {
            super(itemView);
            ivPoster = (ImageView) itemView.findViewById(R.id.grid_item_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if(mListener != null)
                mListener.onItemClick(mMovies.get(pos));
        }
    }
}
