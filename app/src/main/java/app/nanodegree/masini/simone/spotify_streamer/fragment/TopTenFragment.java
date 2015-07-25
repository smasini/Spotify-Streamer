package app.nanodegree.masini.simone.spotify_streamer.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.nanodegree.masini.simone.spotify_streamer.PlaybackActivity;
import app.nanodegree.masini.simone.spotify_streamer.R;
import app.nanodegree.masini.simone.spotify_streamer.adapter.SongAdapter;
import app.nanodegree.masini.simone.spotify_streamer.model.SpotifyTrack;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopTenFragment extends Fragment {

    private String idArtist, nameArtist;
    private SongAdapter adapter;
    private SearchSongTask searchSongTask;
    private ArrayList<SpotifyTrack> tracks = new ArrayList<>();
    public TopTenFragment() { }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_top_ten, container, false);

        Bundle arguments = getArguments();
        if(arguments != null){
            idArtist = arguments.getString(getString(R.string.extra_artist_id_key));
            nameArtist = arguments.getString(getString(R.string.extra_artist_name_key));
        }

        adapter = new SongAdapter(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.listview_top_10);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fm = getActivity().getFragmentManager();
                PlaybackFragment playbackFragment = new PlaybackFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(getString(R.string.extra_tracks_array_key), tracks);
                arguments.putInt(getString(R.string.extra_current_position_key), i);
                playbackFragment.setArguments(arguments);
                if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                    playbackFragment.show(fm, getString(R.string.fragment_dialog_key));
                } else {
                    Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.extra_tracks_array_key), tracks);
                    intent.putExtra(getString(R.string.extra_current_position_key), i);
                    startActivity(intent);
                }
            }
        });

        searchSongTask = new SearchSongTask();
        searchSongTask.execute(idArtist);
        android.support.v7.app.ActionBar ab = ((ActionBarActivity) this.getActivity()).getSupportActionBar();
        if(ab!=null)
            ab.setSubtitle(nameArtist);

        return view;
    }

    public SpotifyTrack getTrack(Track track){
        SpotifyTrack spotifyTrack = new SpotifyTrack();
        spotifyTrack.setAlbumName(track.album.name);
        spotifyTrack.setArtistName(nameArtist);
        spotifyTrack.setId(track.id);
        spotifyTrack.setName(track.name);
        spotifyTrack.setPreviewUrl(track.preview_url);
        if(track.album.images.size()>0 && !track.album.images.get(0).equals(""))
            spotifyTrack.setUrlImage(track.album.images.get(0).url);
        return spotifyTrack;
    }

    public class SearchSongTask extends AsyncTask<String,Void,List<Track>> {
        final String TAG = SearchSongTask.class.getSimpleName();
        @Override
        protected List<Track> doInBackground(String... params) {
            List<Track> tracks = new ArrayList<Track>();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        protected void onPostExecute(List<Track> track) {
            super.onPostExecute(track);
            for(int i = 0;i<track.size();i++) {
                tracks.add(getTrack(track.get(i)));
            }
            adapter.clear();
            adapter.addAll(track);
        }
    }
}
