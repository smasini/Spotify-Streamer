package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import app.nanodegree.masini.simone.spotify_streamer.adapter.ArtistAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


public class SearchArtistActivity extends ActionBarActivity {

    private ArtistAdapter adapterResult;
    private SearchTask searchTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);
        EditText searchArtist = (EditText) findViewById(R.id.edit_text_artist_name);
        ListView resultArtist = (ListView) findViewById(R.id.listview_artist_result);
        searchArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                searchArtist(s.toString());
            }
        });
        adapterResult = new ArtistAdapter(this);
        resultArtist.setAdapter(adapterResult);
        resultArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = adapterResult.getItem(position);
                Intent intent = new Intent(SearchArtistActivity.this,TopTenActivity.class);
                intent.putExtra("artist", artist.id);
                startActivity(intent);
            }
        });

    }

    public void searchArtist(String artistName){
        if(searchTask!=null && !searchTask.isCancelled()){
            searchTask.cancel(true);
        }
        searchTask = new SearchTask();
        searchTask.execute(artistName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_artist, menu);
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


    public class SearchTask extends AsyncTask<String,Void, List<Artist>> {

        @Override
        protected List<Artist> doInBackground(String... params) {
            List<Artist> artists = new ArrayList<>();
            SpotifyApi api = new SpotifyApi();
            SpotifyService service = api.getService();
            try {
                ArtistsPager artistsPager = service.searchArtists(params[0].toLowerCase());
                if (artistsPager != null) {
                    artists = artistsPager.artists.items;
                }
            }catch(RetrofitError e){
                return artists;
            }
            return artists;
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {
            super.onPostExecute(artists);
            if(artists.size()==0){
                Toast.makeText(SearchArtistActivity.this, getString(R.string.msg_no_result), Toast.LENGTH_SHORT).show();
            }
            adapterResult.clear();
            adapterResult.addAll(artists);
        }
    }
}
