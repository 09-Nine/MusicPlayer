package com.nine.musicplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nine.musicplayer.interfaces.AdapterListener;
import com.nine.musicplayer.R;
import com.nine.musicplayer.Utils;
import com.nine.musicplayer.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private final List<Song> songList;
    private AdapterListener listener;

    public void setListener(AdapterListener listener) {
        this.listener = listener;
    }

    public SongAdapter(List<Song> songList) {
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song s = songList.get(position);
        holder.songItemName.setText(s.getName());
        holder.songItemArtist.setText(s.getArtist());
        holder.songItemDuration.setText(Utils.convertMilliToMinuteString(s.getDuration()));
        if (s.getThumbnail() != null) {
            holder.songItemThumbnail.setImageBitmap(s.getThumbnail());
        } else {
            holder.songItemThumbnail.setImageResource(R.drawable.music_placeholder);
        }
        holder.songItemContainer.setOnClickListener(v -> listener.onClick(s));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songItemName, songItemArtist, songItemDuration;
        public ImageView songItemThumbnail;
        public LinearLayout songItemContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songItemName = itemView.findViewById(R.id.song_item_name);
            songItemArtist = itemView.findViewById(R.id.song_item_artist);
            songItemThumbnail = itemView.findViewById(R.id.song_item_thumbnail);
            songItemDuration = itemView.findViewById(R.id.song_item_duration);
            songItemContainer = itemView.findViewById(R.id.song_item_container);
        }
    }

}
