package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import app.nanodegree.masini.simone.spotify_streamer.fragments.PlaybackFragment;


public class PlaybackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Bundle arguments = new Bundle();
        Intent intent = getIntent();
        if(intent.hasExtra(getString(R.string.extra_tracks_array_key)))
            arguments.putParcelableArrayList(getString(R.string.extra_tracks_array_key), intent.getParcelableArrayListExtra(getString(R.string.extra_tracks_array_key)));
        if(intent.hasExtra(getString(R.string.extra_current_position_key)))
            arguments.putInt(getString(R.string.extra_current_position_key), intent.getIntExtra(getString(R.string.extra_current_position_key), 0));
        if(intent.hasExtra(getString(R.string.extra_play_now_key)))
            arguments.putBoolean(getString(R.string.extra_play_now_key), intent.getBooleanExtra(getString(R.string.extra_play_now_key), false));

        PlaybackFragment fragment = new PlaybackFragment();
        fragment.setArguments(arguments);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.container_player, fragment, getString(R.string.fragment_dialog_key))
                .commit();
    }


}
