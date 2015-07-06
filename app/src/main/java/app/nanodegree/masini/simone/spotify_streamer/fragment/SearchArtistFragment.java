package app.nanodegree.masini.simone.spotify_streamer.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.nanodegree.masini.simone.spotify_streamer.R;
import app.nanodegree.masini.simone.spotify_streamer.TopTenActivity;
import app.nanodegree.masini.simone.spotify_streamer.adapter.ArtistAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchArtistFragment extends Fragment {

    private ArtistAdapter mArtistAdapter;
    private SearchTask mSearchTask;
    private String mArtist;
    private final String ARTIST_KEY = "artist_key";

    public SearchArtistFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search_artist, container, false);

        final EditText searchArtist = (EditText) view.findViewById(R.id.edit_text_artist_name);
        ListView resultArtist = (ListView) view.findViewById(R.id.listview_artist_result);

        if(savedInstanceState!=null){
            mArtist = savedInstanceState.getString(ARTIST_KEY);
        }

        searchArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mArtist = s.toString();
                searchArtist();
            }
        });

        mArtistAdapter = new ArtistAdapter(getActivity());
        resultArtist.setAdapter(mArtistAdapter);
        resultArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = mArtistAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TopTenActivity.class);
                intent.putExtra(getString(R.string.extra_artist_id_key), artist.id);
                intent.putExtra(getString(R.string.extra_artist_name_key), artist.name);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(ARTIST_KEY, mArtist);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void searchArtist(){
        if(mSearchTask !=null && !mSearchTask.isCancelled()){
            mSearchTask.cancel(true);
        }
        mSearchTask = new SearchTask();
        mSearchTask.execute(mArtist);
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
                Toast.makeText(getActivity(), getString(R.string.msg_no_result), Toast.LENGTH_SHORT).show();
            }
            mArtistAdapter.clear();
            mArtistAdapter.addAll(artists);
        }
    }

}
