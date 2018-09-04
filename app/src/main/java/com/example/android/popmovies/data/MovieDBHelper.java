package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int VERSION = 1;

    MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID +                  " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_TITLE +         " TEXT NOT NULL, "      +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH +   " TEXT NOT NULL, "      +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE +  " REAL NOT NULL, "      +
                MovieContract.MovieEntry.COLUMN_OVERVIEW +      " TEXT NOT NULL, "      +
                MovieContract.MovieEntry.COLUMN_RUNTIME +       " INTEGER NOT NULL, "   +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE +  " INTEGER NOT NULL); ";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
