package com.example.android.popmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.popmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MovieServiceUtil {

    private static final String TAG = MovieServiceUtil.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static URL buildListUrl(String option, Context context) {

        Uri builtUri = Uri.parse(BASE_URL.concat(option))
                .buildUpon()
                .appendQueryParameter("api_key", context.getString(R.string.api_key))
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to build URL object", e);
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildDetailUrl(Integer movieId, Context context) {

        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(movieId.toString())
                .appendQueryParameter("api_key", context.getString(R.string.api_key))
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to build URL object", e);
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url, Context context) throws IOException {

        if (!isOnline(context)) {
            return null;
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
