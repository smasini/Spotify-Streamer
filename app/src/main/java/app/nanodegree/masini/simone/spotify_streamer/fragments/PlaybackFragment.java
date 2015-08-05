package app.nanodegree.masini.simone.spotify_streamer.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import app.nanodegree.masini.simone.spotify_streamer.MediaConnection;
import app.nanodegree.masini.simone.spotify_streamer.R;
import app.nanodegree.masini.simone.spotify_streamer.models.SpotifyTrack;
import app.nanodegree.masini.simone.spotify_streamer.services.PlaybackService;

/**
 *
 * Created by Simone Masini on 23/07/2015 at 23.38.
 */
public class PlaybackFragment extends DialogFragment{

    private SpotifyTrack currentTrack;
    private ArrayList<SpotifyTrack> tracks;
    private int position = -1;

    public PlaybackFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playback, container, false);
        boolean playNow = false;
        Bundle arguments = getArguments();
        if(arguments != null){
            if(arguments.containsKey(getString(R.string.extra_current_position_key)))
                position = arguments.getInt(getString(R.string.extra_current_position_key));
            if(arguments.containsKey(getString(R.string.extra_tracks_array_key)))
                tracks = arguments.getParcelableArrayList(getString(R.string.extra_tracks_array_key));
            if(arguments.containsKey(getString(R.string.extra_play_now_key)))
                playNow = arguments.getBoolean(getString(R.string.extra_play_now_key));
        }

        TextView artist = (TextView) view.findViewById(R.id.textview_artist_name_track);
        TextView album = (TextView) view.findViewById(R.id.textview_album_name_track);
        TextView trackTitle = (TextView) view.findViewById(R.id.texxtview_track_name);
        ImageView albumArtwork = (ImageView) view.findViewById(R.id.imageview_album_artwork);
        TextView durationTotal = (TextView) view.findViewById(R.id.textview_total_duration);
        TextView durationProgress = (TextView) view.findViewById(R.id.textview_duration_progress);
        ImageButton prev = (ImageButton) view.findViewById(R.id.btn_prev);
        ImageButton play = (ImageButton) view.findViewById(R.id.btn_play);
        ImageButton next = (ImageButton) view.findViewById(R.id.btn_next);
        SeekBar progress = (SeekBar) view.findViewById(R.id.seekbar_progress);

        MediaConnection mediaConnection = MediaConnection.getMediaConnectionInstance();
        mediaConnection.setDurationCurrent(durationProgress);
        mediaConnection.setDurationTotal(durationTotal);
        mediaConnection.setSeekBar(progress);
        mediaConnection.setPlayButton(play);
        mediaConnection.setNextButton(next);
        mediaConnection.setPrevButton(prev);
        mediaConnection.setTrackAlbum(album);
        mediaConnection.setTrackArtist(artist);
        mediaConnection.setTrackTitle(trackTitle);
        mediaConnection.setAlbumThumbnail(albumArtwork);
        if(tracks!=null)
            mediaConnection.setSpotifyTrackList(tracks);
        if(position>0)
            mediaConnection.setPosition(position);
        mediaConnection.setContext(this.getActivity());

        if(playNow){
            position = mediaConnection.getPosition();
            tracks = mediaConnection.getSpotifyTrackList();
            mediaConnection.initUI();
        }else{
            if(mediaConnection.isSomethingPlayingNow()){
                Intent mediaPlayerService = new Intent(getActivity(), PlaybackService.class);
                mediaPlayerService.setAction(PlaybackService.ACTION_STOP);
                mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startService(mediaPlayerService);
            }
            mediaConnection.init();
            Intent mediaPlayerService = new Intent(getActivity(), PlaybackService.class);
            mediaPlayerService.setAction(PlaybackService.ACTION_PLAY);
            mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startService(mediaPlayerService);
        }
        currentTrack = tracks.get(position);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        MediaConnection.getMediaConnectionInstance().startUI();
        if(getDialog()!=null){
            int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
            int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
            getDialog().getWindow().setLayout(width, height);
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        MediaConnection.getMediaConnectionInstance().stopUI();
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_share, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        if(currentTrack.getExternalUrl()!=null) {
            ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentTrack.getExternalUrl());
        return shareIntent;
    }
}
