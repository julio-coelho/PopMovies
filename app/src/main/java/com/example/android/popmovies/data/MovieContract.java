package com.example.android.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        /*
        TB_MOVIE
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        |   _ID   |   TITLE   |   POSTER_PATH   |   VOTE_AVERAGE   |   OVERVIEW   |   RUNTIME   |   RELEASE_DATE   |
         - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
         */

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "TB_MOVIE";

        public static final String COLUMN_TITLE = "TITLE";
        public static final String COLUMN_POSTER_PATH = "POSTER_PATH";
        public static final String COLUMN_VOTE_AVERAGE = "VOTE_AVERAGE";
        public static final String COLUMN_OVERVIEW = "OVERVIEW";
        public static final String COLUMN_RUNTIME = "RUNTIME";
        public static final String COLUMN_RELEASE_DATE = "RELEASE_DATE";
    }

}
