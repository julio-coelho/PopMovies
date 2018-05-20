package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

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
    private Double voteAverage;
    private String overview;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public Movie(JSONObject json) throws JSONException {

        this.id = json.getInt("id");
        this.title = json.getString("title");
        this.posterPath = json.getString("poster_path");
        this.voteAverage = json.getDouble("vote_average");
        this.overview = json.getString("overview");

        try {
            this.releaseDate = sdf.parse(json.getString("release_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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

    public String getFormattedReleaseDate() {
        return sdf.format(releaseDate);
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

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