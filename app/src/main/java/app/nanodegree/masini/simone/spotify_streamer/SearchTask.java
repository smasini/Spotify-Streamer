package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Package: app.nanodegree.masini.simone.spotify_streamer
 * Project: Spotify-Streamer
 * Created by Simone Masini on 14/06/2015 at 10.34.
 */
public class SearchTask extends AsyncTask<String,Void, List<Artist>> {

    private Context context;
    private ListView result;
    public  SearchTask(Context context, ListView result){
        this.context = context;
        this.result = result;
    }

    @Override
    protected List<Artist> doInBackground(String... params) {
        List<Artist> artists = new ArrayList<>();
        SpotifyApi api = new SpotifyApi();
        SpotifyService service = api.getService();
        if(params[0].equals("")){
            return artists;
        }
        ArtistsPager artistsPager = service.searchArtists(params[0]);
        if(artistsPager!=null) {
            artists = artistsPager.artists.items;
        }
        return artists;
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {
        super.onPostExecute(artists);
        if(artists.size()==0){
            Toast.makeText(context, context.getString(R.string.msg_no_result), Toast.LENGTH_SHORT).show();
        }
        for(int i = 0; i< artists.size();i++){

        }
    }
}
