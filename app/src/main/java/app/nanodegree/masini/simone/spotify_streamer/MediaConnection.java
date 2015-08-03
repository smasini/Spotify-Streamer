package app.nanodegree.masini.simone.spotify_streamer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import app.nanodegree.masini.simone.spotify_streamer.models.SpotifyTrack;
import app.nanodegree.masini.simone.spotify_streamer.services.PlaybackService;
import app.nanodegree.masini.simone.spotify_streamer.utils.Utility;

/**
 * Spotify-Streamer
 * ${PACKAGE_NAME}
 * Created by Simone Masini on 01/08/2015 at 23.39.
 */
public class MediaConnection {

    private static MediaConnection mMediaConnection;

    private MediaConnection(){ }

    public static MediaConnection getMediaConnectionInstance(){
        if(mMediaConnection==null)
            mMediaConnection = new MediaConnection();
        return mMediaConnection;
    }
    private SpotifyTrack mSpotifyTrack;
    private ArrayList<SpotifyTrack> mSpotifyTrackList;
    private int mPosition;

    private Activity context;
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;
    private TextView mDurationTotal, mDurationCurrent, mTrackTitle, mTrackArtist, mTrackAlbum;
    private ImageView mAlbumThumbnail;
    private ImageButton mPlayButton, mPrevButton, mNextButton;

    private boolean isNowPlaying = false;

    private TrackProgressTask trackProgressTask;

    public void init(){

        mSpotifyTrack = mSpotifyTrackList.get(mPosition);
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(mSpotifyTrack.getPreviewUrl());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaplayer) {
                Intent mediaPlayerService = new Intent(context, PlaybackService.class);
                mediaPlayerService.setAction(PlaybackService.ACTION_STOP);
                mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startService(mediaPlayerService);
            }
        });
        initUI();
    }

    public boolean isSomethingPlayingNow(){
        return isNowPlaying;
    }

    public void initUI(){
        mDurationTotal.setText(Utility.formatTime(mMediaPlayer.getDuration()));
        mSeekBar.setMax(mMediaPlayer.getDuration());
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
        mTrackArtist.setText(mSpotifyTrack.getArtistName());
        mTrackAlbum.setText(mSpotifyTrack.getAlbumName());
        mTrackTitle.setText(mSpotifyTrack.getName());
        if(!mSpotifyTrack.getUrlImage().equals("")){
            Picasso.with(context).load(mSpotifyTrack.getUrlImage()).into(mAlbumThumbnail);
        }
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mDurationCurrent.setText(Utility.formatTime(i));
                if (i == 0) {
                    mPlayButton.setImageResource(android.R.drawable.ic_media_play);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer.isPlaying()) {
                    Intent mediaPlayerService = new Intent(context, PlaybackService.class);
                    mediaPlayerService.setAction(PlaybackService.ACTION_PAUSE);
                    mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startService(mediaPlayerService);
                } else {
                    Intent mediaPlayerService = new Intent(context, PlaybackService.class);
                    mediaPlayerService.setAction(PlaybackService.ACTION_PLAY);
                    mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startService(mediaPlayerService);
                }
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediaPlayerService = new Intent(context, PlaybackService.class);
                mediaPlayerService.setAction(PlaybackService.ACTION_PREVIOUS);
                mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startService(mediaPlayerService);
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediaPlayerService = new Intent(context, PlaybackService.class);
                mediaPlayerService.setAction(PlaybackService.ACTION_NEXT);
                mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startService(mediaPlayerService);
            }
        });
    }

    public void play(){
        isNowPlaying = true;
        mMediaPlayer.start();
        mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        trackProgressTask = new TrackProgressTask();
        trackProgressTask.execute();
    }

    public void pause(){
        mMediaPlayer.pause();
        mPlayButton.setImageResource(android.R.drawable.ic_media_play);
    }

    public void stop(){
        isNowPlaying = false;
        mMediaPlayer.seekTo(0);
        mSeekBar.setProgress(0);
    }

    public void resetMP(){
        if(mMediaPlayer!=null)
            mMediaPlayer.stop();
        mMediaPlayer = null;
    }

    public void prev(){
        if (mPosition > 0) {
            mPosition = mPosition -1;
            init();
            Intent mediaPlayerService = new Intent(context, PlaybackService.class);
            mediaPlayerService.setAction(PlaybackService.ACTION_PLAY);
            mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startService(mediaPlayerService);
        } else {
            Toast.makeText(context, context.getString(R.string.first_song_message), Toast.LENGTH_LONG).show();
        }
    }

    public void next(){
        if (mPosition < mSpotifyTrackList.size() - 1) {
            mPosition = mPosition + 1;
            init();
            Intent mediaPlayerService = new Intent(context, PlaybackService.class);
            mediaPlayerService.setAction(PlaybackService.ACTION_PLAY);
            mediaPlayerService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startService(mediaPlayerService);
        } else {
            Toast.makeText(context, context.getString(R.string.last_song_message), Toast.LENGTH_LONG).show();
        }
    }

    public void stopUI(){
        if(trackProgressTask != null){
            trackProgressTask.cancel(true);
        }
    }

    public void startUI(){
        if(mMediaPlayer.isPlaying()){
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    public void setSeekBar(SeekBar mSeekBar) {
        this.mSeekBar = mSeekBar;
    }

    public void setDurationTotal(TextView mDurationTotal) {
        this.mDurationTotal = mDurationTotal;
    }

    public void setDurationCurrent(TextView mDurationCurrent) {
        this.mDurationCurrent = mDurationCurrent;
    }

    public void setPrevButton(ImageButton mPrevButton) {
        this.mPrevButton = mPrevButton;
    }

    public void setNextButton(ImageButton mNextButton) {
        this.mNextButton = mNextButton;
    }

    public void setPlayButton(ImageButton mPlayButton) {
        this.mPlayButton = mPlayButton;
    }

    public void setTrackAlbum(TextView mTrackAlbum) {
        this.mTrackAlbum = mTrackAlbum;
    }

    public void setTrackArtist(TextView mTrackArtist) {
        this.mTrackArtist = mTrackArtist;
    }

    public void setTrackTitle(TextView mTrackTitle) {
        this.mTrackTitle = mTrackTitle;
    }

    public void setAlbumThumbnail(ImageView mAlbumThumbnail) {
        this.mAlbumThumbnail = mAlbumThumbnail;
    }

    public void setSpotifyTrackList(ArrayList<SpotifyTrack> spotifyTrackList) {
        this.mSpotifyTrackList = spotifyTrackList;
    }

    public int getPosition() {
        return mPosition;
    }

    public ArrayList<SpotifyTrack> getSpotifyTrackList() {
        return mSpotifyTrackList;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public String getNotificationTitle(){
        return mSpotifyTrack.getName();
    }

    public String getNotificationText(){
        return mSpotifyTrack.getArtistName();
    }

    public Bitmap getNotificationIcon(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(mSpotifyTrack.getUrlImage());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Bitmap defaulfBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            return defaulfBitmap;
        }
    }

    public class TrackProgressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while(mMediaPlayer.isPlaying()){
                onProgressUpdate();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            super.onProgressUpdate(values);
        }
    }
}
