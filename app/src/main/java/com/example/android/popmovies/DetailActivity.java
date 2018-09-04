package com.example.android.popmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.popmovies.data.MovieContract;
import com.example.android.popmovies.model.Movie;
import com.example.android.popmovies.model.Review;
import com.example.android.popmovies.adapter.ReviewAdapter;
import com.example.android.popmovies.model.Video;
import com.example.android.popmovies.adapter.VideosAdapter;
import com.example.android.popmovies.databinding.ActivityDetailBinding;
import com.example.android.popmovies.utils.MovieServiceUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements VideosAdapter.VideoAdapterOnClickHandler, ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private Movie mMovie;
    private VideosAdapter mVideosAdapter;
    private ReviewAdapter mReviewAdapter;

    ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // Trailers Config
        LinearLayoutManager lmTrailer = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.trailer.trailerList.setLayoutManager(lmTrailer);
        mBinding.trailer.trailerList.setHasFixedSize(true);
        mVideosAdapter = new VideosAdapter(this, this);
        mBinding.trailer.trailerList.setAdapter(mVideosAdapter);

        // Review Config
        LinearLayoutManager lmReview = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.review.reviewList.setLayoutManager(lmReview);
        mBinding.review.reviewList.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this, this);
        mBinding.review.reviewList.setAdapter(mReviewAdapter);

        // Tasks
        Intent intentThatStartedThisActivity = getIntent();

        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {

            Integer movieId = intentThatStartedThisActivity.getIntExtra(Intent.EXTRA_TEXT, 0);

            new MovieDetailTask().execute(movieId);
            new MovieVideosTask().execute(movieId);
            new MovieReviewTask().execute(movieId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showData() {
        mBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);

        mBinding.tvMovieTitle.setText(mMovie.getTitle());
        mBinding.tvMovieReleaseDate.setText(getString(R.string.label_release_date, mMovie.getReleaseYear()));
        mBinding.tvMovieRuntime.setText(getString(R.string.label_runtime, mMovie.getRuntime()));
        mBinding.tvMovieVoteAverage.setText(getString(R.string.label_vote_average, mMovie.getVoteAverage()));
        mBinding.tvMovieOverview.setText(mMovie.getOverview());

        if (mMovie.isFavorite()) {
            mBinding.btFavorite.setBackground(getDrawable(android.R.drawable.btn_star_big_on));
        } else {
            mBinding.btFavorite.setBackground(getDrawable(android.R.drawable.btn_star_big_off));
        }

        Picasso.with(this).load(mMovie.getPosterPath()).into(mBinding.ivMovieDetail);
    }

    private void showError() {
        mBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
        mBinding.groupVisibility.setVisibility(View.GONE);
    }

    @Override
    public void onClick(String key) {
        String url = getResources().getString(R.string.url_action_youtube, key);
        String title = getResources().getString(R.string.chooser_title);

        Intent intent = new  Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Intent chooser = Intent.createChooser(intent, title);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    @Override
    public void onClickReview(String url) {
        Intent intent = new  Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void onClickFavorite(View view) {

        if(!mMovie.isFavorite()) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry._ID, mMovie.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, mMovie.getRuntime());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate().getTime());

            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

            if (uri != null) {
                mMovie.setFavorite(true);
                mBinding.btFavorite.setBackground(getDrawable(android.R.drawable.btn_star_big_on));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
            }

        } else {

            Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(mMovie.getId().toString()).build();
            int rowsAffected = getContentResolver().delete(uri, null, null);

            if (rowsAffected > 0) {
                mMovie.setFavorite(false);
                mBinding.btFavorite.setBackground(getDrawable(android.R.drawable.btn_star_big_off));
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
            }

        }
    }

    private boolean IsFavorite(Integer id) {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();

        Cursor cursor = getContentResolver().query(uri, null, null, null, MovieContract.MovieEntry.COLUMN_TITLE);

        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }

        return false;
    }

    class MovieDetailTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie doInBackground(Integer... params) {

            URL url = MovieServiceUtil.buildDetailUrl(params[0]);

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
            mBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result != null) {
                mMovie = result;
                mMovie.setFavorite(IsFavorite(mMovie.getId()));
                showData();
            } else {
                mMovie = null;
                showError();
            }
        }
    }

    class MovieVideosTask extends AsyncTask<Integer, Void, List<Video>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.trailer.pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Video> doInBackground(Integer... params) {

            URL url = MovieServiceUtil.buildVideosUrl(params[0]);

            try {
                String response = MovieServiceUtil.getResponseFromHttpUrl(url, DetailActivity.this);

                if (response != null) {

                    // response as JSON
                    JSONObject responseJson = new JSONObject(response);

                    // results as JSON
                    JSONArray results = responseJson.getJSONArray("results");

                    ArrayList<Video> videos = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        videos.add(new Video(results.getJSONObject(i)));
                    }

                    return videos;
                }

            } catch (IOException e) {
                Log.e(TAG, "Failed to make request", e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG, "Failed to make request", e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Video> videos) {
            super.onPostExecute(videos);
            mBinding.trailer.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (videos != null) {
                mVideosAdapter.setVideos(videos);
            } else {
                showError();
            }
        }
    }

    class MovieReviewTask extends AsyncTask<Integer, Void, List<Review>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBinding.review.pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Review> doInBackground(Integer... params) {

            URL url = MovieServiceUtil.buildReviewsUrl(params[0]);

            try {
                String response = MovieServiceUtil.getResponseFromHttpUrl(url, DetailActivity.this);

                if (response != null) {

                    // response as JSON
                    JSONObject responseJson = new JSONObject(response);

                    // results as JSON
                    JSONArray results = responseJson.getJSONArray("results");

                    ArrayList<Review> reviews = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        reviews.add(new Review(results.getJSONObject(i)));
                    }

                    return reviews;
                }

            } catch (IOException e) {
                Log.e(TAG, "Failed to make request", e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG, "Failed to make request", e);
                e.printStackTrace();
            }

            return  null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            mBinding.review.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (reviews != null) {
                mReviewAdapter.setReviews(reviews);
            } else {
                showError();
            }
        }
    }
}
