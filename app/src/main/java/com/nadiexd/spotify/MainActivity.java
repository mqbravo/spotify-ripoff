package com.nadiexd.spotify;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "15cfbe90ce9446b987163dd48169731d";
    private static final String CLIENT_ID_SECRET = "e701a757984c4845a9b25627ff616159";
    private static final String PLAYLIST_URI = "spotify:user:mausk01:playlist:6wei7Upw7h4ZADK8Z6WVUU";
    private static final String REDIRECT_URI = "comspotifytestsdk://callback";
    private boolean isPlaying;
    private boolean isShuffled;
    private TextView songName;
    private TextView artistName;
    private TextView mShuffleState;
    private ImageView albumCover;
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPlaying = false;
        isShuffled = false;

        songName = findViewById(R.id.mainActivity_songName);
        songName.setSelected(true);

        artistName = findViewById(R.id.mainActivity_artistName);
        albumCover = findViewById(R.id.mainActivity_albumCover);
        mShuffleState = findViewById(R.id.mainActivity_shuffle);

        setPlaylistsRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        mSpotifyAppRemote.getPlayerApi().setShuffle(false);

                        Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
                        // Now you can start interacting with App Remote
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                        Log.d("MainActivity", throwable.getMessage());
                    }
                });
    }
    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("MainActivity", track.name + " by " + track.album.name);

                            changeSongInfo(track.name, track.artist.name,track.imageUri);
                        }
                    }
                });
    }

    public void pauseAndPlay(View view){
        if(isPlaying)
            mSpotifyAppRemote.getPlayerApi().pause();

        else
            mSpotifyAppRemote.getPlayerApi().resume();

        togglePlayingStateAndButton();
    }

    public void skipNext(View view){
        mSpotifyAppRemote.getPlayerApi().skipNext();

        if(!isPlaying)
            togglePlayingStateAndButton();
    }

    public void skipPrevious(View view){
        mSpotifyAppRemote.getPlayerApi().skipPrevious();

        if(!isPlaying)
            togglePlayingStateAndButton();
    }

    public void setShuffleMode(View view){
        toggleShuffleMode();
    }

    private void changeSongInfo(String trackName, String trackArtist,
                                    ImageUri coverUri){

        this.songName.setText(trackName);
        this.artistName.setText(trackArtist);
        mSpotifyAppRemote.getImagesApi().getImage(coverUri).setResultCallback(
                new CallResult.ResultCallback<Bitmap>(){
                    @Override public void onResult(Bitmap bitmap) {
                        albumCover.setImageBitmap(bitmap);
                    }
        });
    }

    private void toggleShuffleMode(){
        isShuffled = !isShuffled;
        mSpotifyAppRemote.getPlayerApi().setShuffle(isShuffled);

        if (isShuffled)
            mShuffleState.setTextColor(getResources().getColor(R.color.button_green));

        else
            mShuffleState.setTextColor(getResources().getColor(R.color.white));

    }

    private void togglePlayingStateAndButton(){
        isPlaying = !isPlaying;

        if(isPlaying) {
            Button button = findViewById(R.id.mainActivity_stop);
            button.setText(getResources().getString(R.string.pause));
        }

        else{
            Button button = findViewById(R.id.mainActivity_stop);
            button.setText(getResources().getString(R.string.play));
        }
    }


    private void setPlaylistsRecyclerView(){
        //The recycler we will be working on
        RecyclerView playlistsRecycler = findViewById(R.id.recycler);

        //Adding the "format" or behaviour the recycler will have
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //And add it to the recycler view
        playlistsRecycler.setLayoutManager(linearLayoutManager);

        //Set its adapter by creating a new adapter from the CardView layout resource
        PlaylistAdapterRecycler postAdapterRecyclerView = new PlaylistAdapterRecycler(getPlaylists(),
                R.layout.cardview_playlist, this);
        playlistsRecycler.setAdapter(postAdapterRecyclerView);
    }

    private ArrayList<Playlist> getPlaylists(){
        ArrayList<Playlist> playlists = new ArrayList<>();
        playlists.add(new Playlist("Trve", "spotify:user:mausk01:playlist:6wei7Upw7h4ZADK8Z6WVUU"));
        playlists.add(new Playlist("Art-ict monkeys & TLSP", "spotify:user:mausk01:playlist:7ncnkEET7kH16dVrbby9BI" ));
        playlists.add(new Playlist("Good trip", "spotify:user:mausk01:playlist:6DhR2kGKDZ3uSYQoevznNl"));

        return playlists;
    }


    public void changePlaylist(String playListURI){
        mSpotifyAppRemote.getPlayerApi().play(playListURI);
    }

}

