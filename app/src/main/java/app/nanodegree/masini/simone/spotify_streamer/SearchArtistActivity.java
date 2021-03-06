package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import app.nanodegree.masini.simone.spotify_streamer.fragments.SearchArtistFragment;
import app.nanodegree.masini.simone.spotify_streamer.fragments.TopTenFragment;
import app.nanodegree.masini.simone.spotify_streamer.services.PlaybackService;

public class SearchArtistActivity extends ActionBarActivity implements SearchArtistFragment.Callback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);
        if (findViewById(R.id.container_top_ten_track) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onDestroy() {
        if(PlaybackService.isRunning()) {
            MediaConnection.getMediaConnectionInstance().resetMP();
            Intent intent = new Intent(this, PlaybackService.class);
            stopService(intent);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String idArtist, String nameArtist) {
        if(mTwoPane){
            Bundle arguments = new Bundle();
            arguments.putString(getString(R.string.extra_artist_id_key), idArtist);
            arguments.putString(getString(R.string.extra_artist_name_key), nameArtist);
            TopTenFragment fragment = new TopTenFragment();
            fragment.setArguments(arguments);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_top_ten_track, fragment)
                    .commit();
        }else{
            Intent intent = new Intent(this, TopTenActivity.class);
            intent.putExtra(getString(R.string.extra_artist_id_key), idArtist);
            intent.putExtra(getString(R.string.extra_artist_name_key), nameArtist);
            startActivity(intent);
        }
    }
}
