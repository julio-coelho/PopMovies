package com.example.android.popmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private final Context mContext;
    private List<Video> mVideos;
    private final VideoAdapterOnClickHandler mClickHandler;

    public VideosAdapter(Context context, VideoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = mVideos.get(position);
        holder.trailerName.setText(video.getName());
    }

    @Override
    public int getItemCount() {
        if (mVideos != null) {
            return mVideos.size();
        }
        return 0;
    }

    public void setVideos(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

    public interface VideoAdapterOnClickHandler {
        void onClick(String key);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView trailerName;

        VideoViewHolder(View view) {
            super(view);
            trailerName = view.findViewById(R.id.tv_trailer_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mVideos.get(getAdapterPosition()).getKey());
        }
    }
}
