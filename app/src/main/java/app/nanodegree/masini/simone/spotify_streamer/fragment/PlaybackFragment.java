package app.nanodegree.masini.simone.spotify_streamer.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import app.nanodegree.masini.simone.spotify_streamer.R;
import app.nanodegree.masini.simone.spotify_streamer.model.SpotifyTrack;

/**
 * Created by User on 23/07/2015.
 */
public class PlaybackFragment extends DialogFragment{

    private SpotifyTrack currentTrack;
    private TextView artist, album, trackTitle, durationTotal, durationProgress;
    private ImageView albumArtwork;
    private ImageButton prev, next, play;
    private MediaPlayer mediaPlayer;
    private SeekBar progress;
    private TrackProgressTask trackProgressTask;
    private ArrayList<SpotifyTrack> tracks;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playback, container, false);

        Bundle arguments = getArguments();
        if(arguments != null){
            position = arguments.getInt(getString(R.string.extra_current_position_key));
            tracks = arguments.getParcelableArrayList(getString(R.string.extra_tracks_array_key));
        }

        currentTrack = tracks.get(position);

        artist = (TextView) view.findViewById(R.id.textview_artist_name_track);
        album = (TextView) view.findViewById(R.id.textview_album_name_track);
        trackTitle = (TextView) view.findViewById(R.id.texxtview_track_name);
        albumArtwork = (ImageView) view.findViewById(R.id.imageview_album_artwork);
        durationTotal = (TextView) view.findViewById(R.id.textview_total_duration);
        durationProgress = (TextView) view.findViewById(R.id.textview_duration_progress);
        prev = (ImageButton) view.findViewById(R.id.btn_prev);
        play = (ImageButton) view.findViewById(R.id.btn_play);
        next = (ImageButton) view.findViewById(R.id.btn_next);
        progress = (SeekBar) view.findViewById(R.id.seekbar_progress);

        initLayout();
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
        super.onStart();
        if(getDialog()!=null){
            int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
            int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
            getDialog().getWindow().setLayout(width, height);
        }
    }

    public void initLayout() {

        artist.setText(currentTrack.getArtistName());
        album.setText(currentTrack.getAlbumName());
        trackTitle.setText(currentTrack.getName());
        if(!currentTrack.getUrlImage().equals("")){
            Picasso.with(this.getActivity()).load(currentTrack.getUrlImage()).into(albumArtwork);
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(currentTrack.getPreviewUrl());
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaplayer) {
                    mediaPlayer.seekTo(0);
                    progress.setProgress(0);
                    play.setImageResource(android.R.drawable.ic_media_play);
                    trackProgressTask.cancel(true);
                }
            });
            progress.setMax(mediaPlayer.getDuration());
            durationTotal.setText(formatTime(mediaPlayer.getDuration()));
            progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    durationProgress.setText(formatTime(i));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    play.setImageResource(android.R.drawable.ic_media_pause);
                    trackProgressTask = new TrackProgressTask();
                    trackProgressTask.execute();
                }else{
                    mediaPlayer.pause();
                    play.setImageResource(android.R.drawable.ic_media_play);
                    trackProgressTask.cancel(true);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position<tracks.size()-1) {
                    mediaPlayer.stop();
                    PlaybackFragment playbackFragment = new PlaybackFragment();
                    Bundle arguments = new Bundle();
                    arguments.putParcelableArrayList(getString(R.string.extra_tracks_array_key), tracks);
                    arguments.putInt(getString(R.string.extra_current_position_key), position + 1);
                    playbackFragment.setArguments(arguments);
                    FragmentManager fm = getFragmentManager();

                    if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                        dismiss();
                        playbackFragment.show(fm, "fragment_edit_name");
                    }else{
                        fm.beginTransaction()
                                .replace(R.id.container_player, playbackFragment)
                                .commit();
                    }

                }else{
                    Toast.makeText(getActivity(), getString(R.string.last_song_message), Toast.LENGTH_LONG).show();
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0) {
                    mediaPlayer.stop();
                    PlaybackFragment playbackFragment = new PlaybackFragment();
                    Bundle arguments = new Bundle();
                    arguments.putParcelableArrayList(getString(R.string.extra_tracks_array_key), tracks);
                    arguments.putInt(getString(R.string.extra_current_position_key), position - 1);
                    playbackFragment.setArguments(arguments);
                    FragmentManager fm = getFragmentManager();

                    if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
                        dismiss();
                        playbackFragment.show(fm, getString(R.string.fragment_dialog_key));
                    } else {
                        fm.beginTransaction()
                                .replace(R.id.container_player, playbackFragment)
                                .commit();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.first_song_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String formatTime(long millis){
        long i = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        String s = "";
        if(i<10)
            s = "0";
        s+=i;
        String timeInMinutes = String.format("%d:"+s, TimeUnit.MILLISECONDS.toMinutes(millis));
        return timeInMinutes;
    }

    @Override
    public void onStop() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            play.setImageResource(android.R.drawable.ic_media_play);
            trackProgressTask.cancel(true);
        }else{

        }
        super.onStop();
    }


    class TrackProgressTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while(mediaPlayer.isPlaying()){
                onProgressUpdate();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            progress.setProgress(mediaPlayer.getCurrentPosition());
            super.onProgressUpdate(values);
        }
    }
}







/*
    protected void displayPopupWindow(View anchorView, View layout, int width, int height) {
        PopupWindow popup = new PopupWindow(this);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(height);
        popup.setWidth(width);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(anchorView);
    }*/
