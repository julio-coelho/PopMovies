package com.example.android.popmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context mContext;
    private List<Review> mReviews;
    private final ReviewAdapterOnClickHandler mClickHandler;

    public ReviewAdapter(Context context, ReviewAdapterOnClickHandler clickHandler){
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviews != null){
            return mReviews.size();
        }
        return 0;
    }

    public void setReviews(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    public interface ReviewAdapterOnClickHandler {
        void onClickReview(String url);
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView author;
        final TextView content;

        ReviewViewHolder(View view) {
            super(view);
            author = view.findViewById(R.id.tv_review_author);
            content = view.findViewById(R.id.tv_review_content);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClickReview(mReviews.get(getAdapterPosition()).getUrl());
        }
    }
}
