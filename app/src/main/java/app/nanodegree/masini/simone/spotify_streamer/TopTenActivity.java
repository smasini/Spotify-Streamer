package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import app.nanodegree.masini.simone.spotify_streamer.fragment.TopTenFragment;


public class TopTenActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);

            Bundle arguments = new Bundle();
            Intent intent = getIntent();
            arguments.putString(getString(R.string.extra_artist_id_key), intent.getStringExtra(getString(R.string.extra_artist_id_key)));
            arguments.putString(getString(R.string.extra_artist_name_key), intent.getStringExtra(getString(R.string.extra_artist_name_key)));
            TopTenFragment fragment = new TopTenFragment();
            fragment.setArguments(arguments);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();

    }




}
