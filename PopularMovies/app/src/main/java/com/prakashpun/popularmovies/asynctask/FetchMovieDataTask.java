package com.prakashpun.popularmovies.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.prakashpun.popularmovies.BuildConfig;
import com.prakashpun.popularmovies.model.Movie;
import com.prakashpun.popularmovies.model.Movies;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by prakash_pun on 12/7/2016.
 * <p>
 * Async class for fetching the movie details
 * <p>
 * To separate the Async class from the activity, created an interface to implement a callback
 * Followed information found at:
 * http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
 */


public class FetchMovieDataTask extends AsyncTask<String, Void, List<Movie>> {

    private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();
    private final String API_KEY = "28a81952513351371748e9d724184e32";

    private Context context;
    private AsyncTaskCompleteListener<List<Movie>> listener;

    public FetchMovieDataTask(Context context, AsyncTaskCompleteListener<List<Movie>> listener) {
        this.context = context;
        this.listener = listener;
    }


    @Override
    protected List<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDatabaseService service = retrofit.create(MovieDatabaseService.class);
        Call<Movies> call = service.discoverMovies(params[0],
                BuildConfig.MOVIE_DATABASE_API_KEY);
        try {
            Response<Movies> response = call.execute();
            Movies movies = response.body();
            return movies.getMovies();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while fetching Movie data ", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        listener.onTaskComplete(movies);

    }

}
