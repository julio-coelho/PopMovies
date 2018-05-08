package com.example.android.popmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private final ClickHandler mClickHandler;

    public MovieAdapter(@NonNull Context context, List<Movie> movies, ClickHandler clickHandler) {
        super(context, 0, movies);
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;

        final Movie movie = getItem(position);

        // Recycler process
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
            holder = new ViewHolder();
            holder.posterImageView = convertView.findViewById(R.id.iv_movie_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(getContext()).load(movie.getPosterPath()).into(holder.posterImageView);

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mClickHandler.onClick(movie);
            }
        });

        return convertView;
    }

    public interface ClickHandler {
        void onClick(Movie movie);
    }

    static class ViewHolder {
        ImageView posterImageView;
    }
}
