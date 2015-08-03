package app.nanodegree.masini.simone.spotify_streamer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import app.nanodegree.masini.simone.spotify_streamer.MediaConnection;
import app.nanodegree.masini.simone.spotify_streamer.R;

public class PlaybackService extends Service {

    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private MediaConnection mMediaConnection;

    private static PlaybackService playbackService;

    public PlaybackService() {
    }

    public static boolean isRunning(){
        return playbackService != null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        playbackService = this;
        mMediaConnection = MediaConnection.getMediaConnectionInstance();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.cancel(NOTIFICATION_ID);
        playbackService = null;
    }

    private void handleIntent( Intent intent ) {
        Log.d("", "");
        if( intent == null || intent.getAction() == null )
            return;
        String action = intent.getAction();
        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                buildNotification( createAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ) );
            mMediaConnection.play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                buildNotification(createAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
            mMediaConnection.pause();
        }else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            mMediaConnection.prev();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            mMediaConnection.next();
        }else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                buildNotification( createAction( android.R.drawable.ic_media_play, "Play", ACTION_PLAY ) );
            mMediaConnection.stop();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void buildNotification( Notification.Action action ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notification = sharedPreferences.getBoolean(getString(R.string.pref_enable_notifications_key), false);
        if(notification) {
            Notification.MediaStyle style = new Notification.MediaStyle();

            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(mMediaConnection.getNotificationIcon())
                    .setContentTitle(mMediaConnection.getNotificationTitle())
                    .setContentText(mMediaConnection.getNotificationText())
                    .setStyle(style);

            builder.addAction(createAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
            builder.addAction(action);
            builder.addAction(createAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));

            style.setShowActionsInCompactView(0, 1, 2);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private Notification.Action createAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), PlaybackService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mMediaConnection.resetMP();
        Intent intent = new Intent(this, PlaybackService.class);
        stopService(intent);
        super.onTaskRemoved(rootIntent);
    }
}
