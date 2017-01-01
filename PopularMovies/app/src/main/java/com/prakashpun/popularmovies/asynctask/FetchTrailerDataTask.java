package com.prakashpun.popularmovies.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.prakashpun.popularmovies.BuildConfig;
import com.prakashpun.popularmovies.model.Trailer;
import com.prakashpun.popularmovies.model.Trailers;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by prakash_pun on 12/29/2016.
 */

public class FetchTrailerDataTask extends AsyncTask<Long,Void,List<Trailer>> {

    private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();
    private Context context;
    private AsyncTaskCompleteListener<List<Trailer>> listener;

    public FetchTrailerDataTask(Context context, AsyncTaskCompleteListener<List<Trailer>> listener) {
        this.context = context;
        this.listener = listener;
    }


    @Override
    protected List<Trailer> doInBackground(Long... params) {
        if (params.length == 0) {
            return null;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MovieDatabaseService service = retrofit.create(MovieDatabaseService.class);
        Call<Trailers> call = service.getTrailersById(params[0],
                BuildConfig.MOVIE_DATABASE_API_KEY);
        try {
            Response<Trailers> response = call.execute();
            Trailers trailers = response.body();
            return trailers.getTrailers();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while fetching trailer data ", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        super.onPostExecute(trailers);
        listener.onTaskComplete(trailers);
    }
}
