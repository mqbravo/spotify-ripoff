package com.nadiexd.spotify;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        artistName = findViewById(R.id.mainActivity_artistName);
        albumCover = findViewById(R.id.mainActivity_albumCover);
        mShuffleState = findViewById(R.id.mainActivity_shuffle);
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

        setInitialPlayingStatus();
    }

    public void pauseAndPlay(View view){
        if(isPlaying)
            mSpotifyAppRemote.getPlayerApi().pause();

        else
            mSpotifyAppRemote.getPlayerApi().resume();

        isPlaying = !isPlaying;

        togglePlayingStateButton();

    }

    public void skipNext(View view){
        mSpotifyAppRemote.getPlayerApi().skipNext();

    }

    public void skipPrevious(View view){
        mSpotifyAppRemote.getPlayerApi().skipPrevious();
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

    private void togglePlayingStateButton(){
        if(isPlaying) {
            Button button = findViewById(R.id.mainActivity_stop);
            button.setText(getResources().getString(R.string.pause));
        }

        else{
            Button button = findViewById(R.id.mainActivity_stop);
            button.setText(getResources().getString(R.string.play));
        }
    }

    private void setInitialPlayingStatus(){
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(
                new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState playerState) {
                        isPlaying = !playerState.isPaused;
                        togglePlayingStateButton();
                    }
                }
        );
    }
}

