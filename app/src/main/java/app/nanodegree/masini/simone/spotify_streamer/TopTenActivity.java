package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.nanodegree.masini.simone.spotify_streamer.adapter.SongAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class TopTenActivity extends ActionBarActivity {

    private String idArtist, nameArtist;
    private SongAdapter adapter;
    private SearchSongTask searchSongTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);
        Intent i = getIntent();
        idArtist = i.getStringExtra(getString(R.string.extra_artist_id_key));
        nameArtist = i.getStringExtra(getString(R.string.extra_artist_name_key));
        adapter = new SongAdapter(this);
        ListView listView = (ListView) findViewById(R.id.listview_top_10);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(searchSongTask!=null && !searchSongTask.isCancelled())
            searchSongTask.cancel(true);
        searchSongTask = new SearchSongTask();
        searchSongTask.execute(idArtist);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if(ab!=null)
            ab.setSubtitle(nameArtist);
    }

    public class SearchSongTask extends AsyncTask<String,Void,List<Track>> {
        final String TAG = SearchSongTask.class.getSimpleName();
        @Override
        protected List<Track> doInBackground(String... params) {
            List<Track> tracks = new ArrayList<Track>();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TopTenActivity.this);
            final String countryCode = preferences.getString(getString(R.string.pref_country_code_key),getString(R.string.pref_country_code_default));
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestInterceptor.RequestFacade request) {
                            request.addQueryParam("country", countryCode);
                        }
                    })
                    .build();
            SpotifyService service = restAdapter.create(SpotifyService.class);
            try {
                Tracks tracks1 = service.getArtistTopTrack(params[0]);
                if(tracks1.tracks.size()>0)
                    tracks = service.getArtistTopTrack(params[0]).tracks;
            }catch(RetrofitError e){
                Log.e(TAG, e.toString());
            }
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
