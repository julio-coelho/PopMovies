package com.example.android.popmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popmovies.utils.MovieServiceUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMovieVoteAverage;
    private ImageView mMoviePoster;
    private TextView mMovieOverview;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private Movie mMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieReleaseDate = findViewById(R.id.tv_movie_release_date);
        mMovieVoteAverage = findViewById(R.id.tv_movie_vote_average);
        mMoviePoster = findViewById(R.id.iv_movie_detail);
        mMovieOverview = findViewById(R.id.tv_movie_overview);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        Intent intentThatStartedThisActivity = getIntent();

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {

            Integer movieId = intentThatStartedThisActivity.getIntExtra(Intent.EXTRA_TEXT, 0);

            new MovieDetailTask().execute(movieId);
        }
    }

    private void showData() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        mMovieTitle.setText(mMovie.getTitle());
        mMovieTitle.setVisibility(View.VISIBLE);

        mMovieReleaseDate.setText(getString(R.string.label_release_date, mMovie.getReleaseDate()));
        mMovieReleaseDate.setVisibility(View.VISIBLE);

        mMovieVoteAverage.setText(getString(R.string.label_vote_average, mMovie.getVoteAverage()));
        mMovieVoteAverage.setVisibility(View.VISIBLE);

        Picasso.with(this).load(mMovie.getPosterPath()).into(mMoviePoster);
        mMoviePoster.setVisibility(View.VISIBLE);

        mMovieOverview.setText(mMovie.getOverview());
        mMovieOverview.setVisibility(View.VISIBLE);
    }

    private void showError() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mMovieTitle.setVisibility(View.INVISIBLE);
    }

    public class MovieDetailTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(Integer... params) {

            URL url = MovieServiceUtil.buildDetailUrl(params[0], DetailActivity.this);

            try {
                // response as text
                String response = MovieServiceUtil.getResponseFromHttpUrl(url, DetailActivity.this);

                if (response != null) {
                    return new Movie(new JSONObject(response));
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
        protected void onPostExecute(Movie result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result != null) {
                mMovie = result;
                showData();
            } else {
                mMovie = null;
                showError();
            }
        }
    }
}
