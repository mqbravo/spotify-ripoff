package com.nadiexd.spotify;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistAdapterRecycler extends RecyclerView.Adapter<PlaylistAdapterRecycler.PlaylistViewHolder> {

    private ArrayList<Playlist> playlists;
    private int resource;
    private MainActivity activity;

    public PlaylistAdapterRecycler(ArrayList<Playlist> playlists, int resource, MainActivity activity) {
        this.playlists = playlists;
        this.resource = resource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(resource, viewGroup, false);

        //Pass the view with the card resource
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder playlistViewHolder, int i) {
        final Playlist playlist = playlists.get(i);
        playlistViewHolder.name.setText(playlist.getName());
        playlistViewHolder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changePlaylist(playlist.getUri());
            }
        });

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private LinearLayout frameLayout;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.playlistNameCard);
            frameLayout = itemView.findViewById(R.id.playlistNameLayout);
        }
    }
}
