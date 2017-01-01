package com.prakashpun.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.prakashpun.popularmovies.R;
import com.prakashpun.popularmovies.activities.MovieDetailsActivity;
import com.prakashpun.popularmovies.data.MovieContract;
import com.prakashpun.popularmovies.fragments.MovieDetailFragment;
import com.prakashpun.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prakash_pun on 12/23/2016.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieRecyclerAdapter.ViewHolder>{

    @SuppressWarnings("unused")
    private final static String LOG_TAG = MovieRecyclerAdapter.class.getSimpleName();

    private final List<Movie> mMovies;
    private Context context;

    public MovieRecyclerAdapter(Context context,ArrayList<Movie> movies){
        this.context = context;
        this.mMovies = movies;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_list_content, viewGroup, false);
        int gridColsNumber = context.getResources().getInteger(R.integer.grid_cols);
        view.getLayoutParams().height = (int) (viewGroup.getWidth() / gridColsNumber *
                Movie.POSTER_ASPECT_RATIO);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                Movie movieDetails = mMovies.get(position);
                Intent startDetailActivity = new Intent(context,MovieDetailsActivity.class)
                        .putExtra(MovieDetailFragment.MOVIE_DETAILS,movieDetails);

                context.startActivity(startDetailActivity);

            }
        });

        Picasso.with(context)
                .load(mMovies.get(position).getPosterUrl(context))
                .placeholder((R.drawable.movie_placeholder))
                .error(R.drawable.movie_placeholder)
                .into(viewHolder.moviePosterImage);

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void add(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public void add(Cursor cursor) {
        mMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP_PATH);
                Movie movie = new Movie(id, title, posterPath, overview, rating, releaseDate, backdropPath);
                mMovies.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;

        private ImageView moviePosterImage;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            moviePosterImage = (ImageView) view.findViewById(R.id.thumbnail);
        }

    }
}
