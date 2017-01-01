package com.prakashpun.popularmovies.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prakashpun.popularmovies.R;
import com.prakashpun.popularmovies.activities.MovieDetailsActivity;
import com.prakashpun.popularmovies.adapter.ReviewRecyclerAdapter;
import com.prakashpun.popularmovies.adapter.TrailerRecyclerAdapter;
import com.prakashpun.popularmovies.asynctask.AsyncTaskCompleteListener;
import com.prakashpun.popularmovies.asynctask.FetchReviewDataTask;
import com.prakashpun.popularmovies.asynctask.FetchTrailerDataTask;
import com.prakashpun.popularmovies.model.Movie;
import com.prakashpun.popularmovies.model.Review;
import com.prakashpun.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailFragment extends Fragment{

    @SuppressWarnings("unused")
    public static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static final String MOVIE_DETAILS = "MOVIE_DETAILS";
    public static final String MOVIE_TRAILERS = "MOVIE_TRAILERS";
    public static final String MOVIE_REVIEWS = "MOVIE_REVIEWS";

    private Movie movieDetail;

    List<Review> reviews = new ArrayList<Review>();
    List<Trailer> trailers = new ArrayList<Trailer>();

    @BindView(R.id.movie_title)
    TextView movieTitleView;
    @BindView(R.id.movie_overview)
    TextView mMovieOverviewView;
    @BindView(R.id.movie_release_date)
    TextView mMovieReleaseDateView;
    @BindView(R.id.movie_user_rating)
    TextView mMovieRatingView;

    @BindView(R.id.review_list)
    RecyclerView mRecyclerViewForReviews;

    @BindView(R.id.trailer_list)
    RecyclerView mRecyclerViewForTrailers;

    private ReviewRecyclerAdapter reviewRecyclerAdapter;
    private TrailerRecyclerAdapter trailerRecyclerAdapter;
    private ShareActionProvider mShareActionProvider;


    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(MOVIE_DETAILS)) {
            movieDetail = getArguments().getParcelable(MOVIE_DETAILS);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof MovieDetailsActivity) {
            appBarLayout.setTitle(movieDetail.getTitle());
        }

        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.movie_backdrop));
        if (movieBackdrop != null) {
            Picasso.with(activity)
                    .load(movieDetail.getBackdropUrl(getContext()))
                    .placeholder((R.drawable.movie_placeholder))
                    .error(R.drawable.movie_placeholder)
                    .into(movieBackdrop);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_detail_fragment, container, false);
        ButterKnife.bind(this, view);

        movieTitleView.setText(movieDetail.getTitle());
        mMovieOverviewView.setText(movieDetail.getOverview());
        mMovieRatingView.setText(movieDetail.getUserRating());
        mMovieReleaseDateView.setText(movieDetail.getReleaseDate(getActivity()));

        // For horizontal list of trailers
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewForTrailers.setLayoutManager(layoutManager);
        trailerRecyclerAdapter = new TrailerRecyclerAdapter(getActivity(),new ArrayList<Trailer>());
        mRecyclerViewForTrailers.setAdapter(trailerRecyclerAdapter);
        mRecyclerViewForTrailers.setNestedScrollingEnabled(false);

        // For vertical list of reviews
        reviewRecyclerAdapter = new ReviewRecyclerAdapter(getActivity(),new ArrayList<Review>());
        mRecyclerViewForReviews.setAdapter(reviewRecyclerAdapter);


        // Fetch reviews only if savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_REVIEWS)) {
            reviews = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS);
            reviewRecyclerAdapter.add(reviews);
        } else {
            if(isNetworkAvailable())
                fetchReviews();
            else
                Toast.makeText(getContext(), "Please get online to get movie reviews", Toast.LENGTH_SHORT).show();

        }

        // Fetch reviews only if savedInstanceState == null
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_TRAILERS)) {
            trailers = savedInstanceState.getParcelableArrayList(MOVIE_TRAILERS);
            trailerRecyclerAdapter.add(trailers);
        } else {
            if(isNetworkAvailable())
                fetchTrailers();
            else
                Toast.makeText(getContext(), "Please get online to get movie reviews", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        ArrayList<Review> reviews = reviewRecyclerAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(MOVIE_REVIEWS, reviews);
        }

        ArrayList<Trailer> trailers = trailerRecyclerAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(MOVIE_TRAILERS, trailers);
        }
    }

    private void fetchReviews() {
        FetchReviewDataTask fetchMovieReviewTask = new FetchReviewDataTask(getContext(),
                new AsyncTaskCompleteListener<List<Review>>() {
                    @Override
                    public void onTaskComplete(List<Review> result) {
                        reviews.clear();
                        reviews.addAll(result);
                        reviewRecyclerAdapter.add(reviews);
                    }
                });
        fetchMovieReviewTask.execute(movieDetail.getId());
    }


    private void fetchTrailers() {
        FetchTrailerDataTask fetchTrailerDataTask = new FetchTrailerDataTask(getContext(),
                new AsyncTaskCompleteListener<List<Trailer>>() {
                    @Override
                    public void onTaskComplete(List<Trailer> result) {
                        trailers.clear();
                        trailers.addAll(result);
                        trailerRecyclerAdapter.add(trailers);

                        if (trailerRecyclerAdapter.getItemCount() > 0) {
                            Trailer trailer = trailerRecyclerAdapter.getTrailers().get(0);
                            updateShareActionProvider(trailer);
                        }
                    }
                });
        fetchTrailerDataTask.execute(movieDetail.getId());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_detail_fragment, menu);
        MenuItem shareTrailerMenuItem = menu.findItem(R.id.share_trailer);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareTrailerMenuItem);
    }

    private void updateShareActionProvider(Trailer trailer) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, movieDetail.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailer.getName() + ": "
                + trailer.getTrailerUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }

    //To check if network is available
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}

