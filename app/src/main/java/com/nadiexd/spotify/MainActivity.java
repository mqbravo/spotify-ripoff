package com.nadiexd.spotify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "15cfbe90ce9446b987163dd48169731d";
    private static final String CLIENT_ID_SECRET = "e701a757984c4845a9b25627ff616159";
    private static final String PLAYLIST_URI = "spotify:user:mausk01:playlist:6wei7Upw7h4ZADK8Z6WVUU";
    private static final String REDIRECT_URI = "comspotifytestsdk://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        Toast.makeText(MainActivity.this,"XD",Toast.LENGTH_LONG).show();
                        // Now you can start interacting with App Remote
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Toast.makeText(MainActivity.this,"Me cago en Dios",Toast.LENGTH_LONG).show();
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
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play(PLAYLIST_URI);

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("MainActivity", track.name + " by " + track.artist.name);
                        }
                    }
                });
    }

    public void parar(View view){
        mSpotifyAppRemote.getPlayerApi().skipNext();
    }
}

