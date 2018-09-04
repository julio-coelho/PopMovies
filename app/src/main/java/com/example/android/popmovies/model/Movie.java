package com.example.android.popmovies.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popmovies.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movie implements Parcelable {

    private Integer id;
    private String title;
    private Date releaseDate;
    private String posterPath;
    private Integer runtime;
    private Double voteAverage;
    private String overview;
    private Boolean favorite;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");

    public Movie(JSONObject json) throws JSONException {

        this.id = json.getInt("id");
        this.title = json.getString("title");
        this.posterPath = json.getString("poster_path");
        this.voteAverage = json.getDouble("vote_average");
        this.overview = json.getString("overview");
        this.runtime = json.optInt("runtime", 0);

        try {
            this.releaseDate = sdf.parse(json.getString("release_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.favorite = false;
    }

    public Movie(Cursor cursor) {

        this.id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
        this.title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        this.posterPath = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)).replaceAll("http://image.tmdb.org/t/p/w185", "");
        this.voteAverage = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        this.overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        this.runtime = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME));
        this.releaseDate = new Date(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
        this.favorite = true;
    }

    private Movie(Parcel in) {

        this.id = in.readInt();
        this.title = in.readString();
        try {
            this.releaseDate = sdf.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.posterPath = in.readString();
        this.voteAverage = in.readDouble();
        this.overview = in.readString();
        this.favorite = Boolean.valueOf(in.readString());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() { return releaseDate; }

    public String getFormattedReleaseDate() { return sdf.format(releaseDate); }

    public String getReleaseYear() {
        return yearFormatter.format(releaseDate);
    }

    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterPath() {
        return "http://image.tmdb.org/t/p/w185" + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Boolean isFavorite() { return favorite; }

    public void setFavorite(Boolean favorite) { this.favorite = favorite; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(sdf.format(releaseDate));
        dest.writeString(posterPath);
        dest.writeDouble(voteAverage);
        dest.writeString(overview);
        dest.writeInt(runtime);
        dest.writeString(favorite.toString());
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}