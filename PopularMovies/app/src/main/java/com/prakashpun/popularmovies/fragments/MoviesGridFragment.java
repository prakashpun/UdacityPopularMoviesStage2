package com.prakashpun.popularmovies.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prakashpun.popularmovies.R;
import com.prakashpun.popularmovies.adapter.MovieRecyclerAdapter;
import com.prakashpun.popularmovies.asynctask.AsyncTaskCompleteListener;
import com.prakashpun.popularmovies.asynctask.FetchMovieDataTask;
import com.prakashpun.popularmovies.data.MovieContract;
import com.prakashpun.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String STORED_MOVIES = "stored_movies";
    private MovieRecyclerAdapter movieRecyclerAdapter;

    private static final int FAVORITES_MOVIES_LOADER = 0;

    private SharedPreferences prefs;
    String sortOrder;

    List<Movie> movies = new ArrayList<Movie>();

    public MoviesGridFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder = prefs.getString(getString(R.string.preferences_sort_order_key),
                getString(R.string.preferences_sort_default_value));


        if (savedInstanceState != null) {
            ArrayList<Movie> storedMovies = new ArrayList<Movie>();
            storedMovies = savedInstanceState.<Movie>getParcelableArrayList(STORED_MOVIES);
            movies.clear();
            movies.addAll(storedMovies);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> storedMovies = new ArrayList<Movie>();
        storedMovies.addAll(movies);
        outState.putParcelableArrayList(STORED_MOVIES, storedMovies);

        if (!sortOrder.equals("favorites")) {
            getActivity().getSupportLoaderManager().destroyLoader(FAVORITES_MOVIES_LOADER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.movies_main_fragment, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),getResources()
                .getInteger(R.integer.grid_cols)));

        // get sort order to see if it has recently changed
        String prefSortOrder = prefs.getString(getString(R.string.preferences_sort_order_key),
                getString(R.string.preferences_sort_default_value));

        movieRecyclerAdapter = new MovieRecyclerAdapter(getActivity(),new ArrayList<Movie>());
        recyclerView.setAdapter(movieRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // get sort order to see if it has recently changed
        String prefSortOrder = prefs.getString(getString(R.string.preferences_sort_order_key),
                getString(R.string.preferences_sort_default_value));

        if (movies.size() > 0 && prefSortOrder.equals(sortOrder)) {
            updatePosterAdapter();
        } else {
            sortOrder = prefSortOrder;
            if(isNetworkAvailable()) {
                getMovies();
            }
            else {
                if(sortOrder.equals("favorites"))
                    getActivity().getSupportLoaderManager().initLoader(FAVORITES_MOVIES_LOADER, null, this);
                Toast.makeText(getContext(), "Application is offline. Please retry", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        movieRecyclerAdapter.add(cursor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }
    private void getMovies() {
        if (!sortOrder.equals("favorites")) {
            FetchMovieDataTask fetchMovieDataTask = new FetchMovieDataTask(getContext(),
                    new AsyncTaskCompleteListener<List<Movie>>() {
                        @Override
                        public void onTaskComplete(List<Movie> result) {
                            movies.clear();
                            movies.addAll(result);
                            updatePosterAdapter();
                        }
                    });
            fetchMovieDataTask.execute(sortOrder);
        }else{
            getActivity().getSupportLoaderManager().initLoader(FAVORITES_MOVIES_LOADER, null, this);
        }

    }

    // updates the ArrayAdapter of poster images
    private void updatePosterAdapter() {
        movieRecyclerAdapter.add(movies);
    }

    //To check if network is available
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
