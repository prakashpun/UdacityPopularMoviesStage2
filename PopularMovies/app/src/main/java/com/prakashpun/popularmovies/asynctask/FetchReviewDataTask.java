package com.prakashpun.popularmovies.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.prakashpun.popularmovies.BuildConfig;
import com.prakashpun.popularmovies.model.Review;
import com.prakashpun.popularmovies.model.Reviews;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by prakash_pun on 12/29/2016.
 */

public class FetchReviewDataTask extends AsyncTask<Long,Void,List<Review>> {

    private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();
    private Context context;
    private AsyncTaskCompleteListener<List<Review>> listener;

    public FetchReviewDataTask(Context context, AsyncTaskCompleteListener<List<Review>> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Review> doInBackground(Long... params) {
        if (params.length == 0) {
            return null;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        MovieDatabaseService service = retrofit.create(MovieDatabaseService.class);
        Call<Reviews> call = service.getReviewsById(params[0],
                BuildConfig.MOVIE_DATABASE_API_KEY);
        try {
            Response<Reviews> response = call.execute();
            Reviews reviews = response.body();
            return reviews.getReviews();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while fetching Review data ", e);
        }
        return null;
    }


    @Override
    protected void onPostExecute(List<Review> reviews) {
        super.onPostExecute(reviews);
        listener.onTaskComplete(reviews);

    }

}
