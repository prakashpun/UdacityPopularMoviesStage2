package com.prakashpun.popularmovies.asynctask;

import com.prakashpun.popularmovies.model.Movies;
import com.prakashpun.popularmovies.model.Reviews;
import com.prakashpun.popularmovies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by prakash_pun on 12/21/2016.
 */

public interface MovieDatabaseService {

    @GET("3/movie/{sort_by}")
    Call<Movies> discoverMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<Trailers> getTrailersById(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<Reviews> getReviewsById(@Path("id") long movieId, @Query("api_key") String apiKey);
}
