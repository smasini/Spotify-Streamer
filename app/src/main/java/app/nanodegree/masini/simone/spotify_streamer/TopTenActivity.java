package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.nanodegree.masini.simone.spotify_streamer.adapter.SongAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class TopTenActivity extends ActionBarActivity {

    private String idArtist;
    private SongAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);
        Intent i = getIntent();
        idArtist = i.getStringExtra("artist");
        adapter = new SongAdapter(this);
        ListView listView = (ListView) findViewById(R.id.listview_top_10);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new SearchSongTask().execute(idArtist);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_ten, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SearchSongTask extends AsyncTask<String,Void,List<Track>> {
        @Override
        protected List<Track> doInBackground(String... params) {
            List<Track> tracks = new ArrayList<Track>();
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();
            Tracks tracks1 = service.getArtistTopTrack(params[0]);
            if(tracks1.tracks.size()>0)
                tracks = service.getArtistTopTrack(params[0]).tracks;
            return tracks;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            super.onPostExecute(tracks);
            adapter.clear();
            adapter.addAll(tracks);
        }
    }
}
