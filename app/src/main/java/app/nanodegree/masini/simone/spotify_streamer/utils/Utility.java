package app.nanodegree.masini.simone.spotify_streamer.utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by User on 01/08/2015.
 */
public class Utility {

    public static String formatTime(long millis){
        long i = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        String s = "";
        if(i<10)
            s = "0";
        s+=i;
        String timeInMinutes = String.format("%d:"+s, TimeUnit.MILLISECONDS.toMinutes(millis));
        return timeInMinutes;
    }
}
