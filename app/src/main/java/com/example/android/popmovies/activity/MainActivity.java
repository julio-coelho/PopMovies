package com.example.android.popmovies.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popmovies.Movie;
import com.example.android.popmovies.MovieAdapter;
import com.example.android.popmovies.R;
import com.example.android.popmovies.utils.MovieServiceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private GridView mGridView;

    private ArrayList<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = findViewById(R.id.gv_movies);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            new MovieTask().execute("popular");
        } else {
            mMovies = savedInstanceState.getParcelableArrayList("movies");

            if(mMovies != null) {
                mMovieAdapter = new MovieAdapter(MainActivity.this, mMovies, MainActivity.this);
                mGridView.setAdapter(mMovieAdapter);
            } else {
                new MovieTask().execute("popular");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", mMovies);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_popularity) {
            new MovieTask().execute("popular");
            return true;
        } else if (id == R.id.action_sort_top_rated) {
            new MovieTask().execute("top_rated");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showData() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(Movie movie) {

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movie.getId());
        startActivity(intent);
    }

    class MovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMovieAdapter = null;
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            String option = "popular";

            if (params.length > 0) {
                option = params[0];
            }

            URL url = MovieServiceUtil.buildListUrl(option);

            try {
                // response as text
                String response = MovieServiceUtil.getResponseFromHttpUrl(url, MainActivity.this);

                if (response != null) {

                    // response as JSON
                    JSONObject responseJson = new JSONObject(response);

                    // results as JSON
                    JSONArray results = responseJson.getJSONArray("results");

                    ArrayList<Movie> movies = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        movies.add(new Movie(results.getJSONObject(i)));
                    }

                    return movies;
                }

            } catch (IOException e) {
                Log.e(TAG, "Failed to make request", e);
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to parse response", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result != null) {
                showData();
                mMovies = result;
                mMovieAdapter = new MovieAdapter(MainActivity.this, mMovies, MainActivity.this);
                mGridView.setAdapter(mMovieAdapter);
            } else {
                mMovies = null;
                showError();
            }
        }
    }

}
