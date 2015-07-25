package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import app.nanodegree.masini.simone.spotify_streamer.fragment.PlaybackFragment;


public class PlaybackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Bundle arguments = new Bundle();
        Intent intent = getIntent();
        arguments.putParcelableArrayList(getString(R.string.extra_tracks_array_key), intent.getParcelableArrayListExtra(getString(R.string.extra_tracks_array_key)));
        arguments.putInt(getString(R.string.extra_current_position_key),intent.getIntExtra(getString(R.string.extra_current_position_key), 0));
        PlaybackFragment fragment = new PlaybackFragment();
        fragment.setArguments(arguments);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container_player, fragment)
                .commit();
    }


}
