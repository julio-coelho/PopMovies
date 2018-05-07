package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

public class Movie implements Parcelable {

    private Integer id;
    private String title;
    private LocalDate releaseDate;
    private String posterPath;
    private Double voteAverage;
    private String overview;

    public Movie(JSONObject json) throws JSONException {

        this.id = json.getInt("id");
        this.title = json.getString("title");
        this.releaseDate = LocalDate.parse(json.getString("release_date"));
        this.posterPath = json.getString("poster_path");
        this.voteAverage = json.getDouble("vote_average");
        this.overview = json.getString("overview");
    }

    private Movie(Parcel in) {

        this.id = in.readInt();
        this.title = in.readString();
        this.releaseDate = LocalDate.parse(in.readString());
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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
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
        dest.writeString(releaseDate.toString());
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