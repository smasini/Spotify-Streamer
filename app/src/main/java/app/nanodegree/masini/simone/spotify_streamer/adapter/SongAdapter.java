package app.nanodegree.masini.simone.spotify_streamer.adapter;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.nanodegree.masini.simone.spotify_streamer.R;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Package: app.nanodegree.masini.simone.spotify_streamer.adapter
 * Project: Spotify-Streamer
 * Created by Simone Masini on 14/06/2015 at 23.44.
 */
public class SongAdapter extends ArrayAdapter<Track> {

    private ActionBarActivity context;

    public SongAdapter(Context context){
        super(context, R.layout.top_ten_row);
        this.context = (ActionBarActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView==null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.top_ten_row, parent, false);
            TextView song = (TextView) convertView.findViewById(R.id.textview_song_title);
            TextView album = (TextView) convertView.findViewById(R.id.textview_song_album);
            ImageView photo = (ImageView) convertView.findViewById(R.id.imageview_thumbnail_song);
            holder = new ViewHolder(song,album, photo);
            convertView.setTag(holder);
        }

        if (holder == null && convertView != null) {
            Object tag = convertView.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        Track track = getItem(position);

        if(track!=null && holder !=null){
            holder.textHolderSongTitle.setText(track.name);
            holder.textHolderAlbum.setText(track.album.name);
            if(track.album.images.size()>0)
                Picasso.with(context).load(track.album.images.get(0).url).into(holder.holderImage);

        }
        return convertView;
    }

    public static class ViewHolder {
        public final TextView textHolderSongTitle, textHolderAlbum;
        public final ImageView holderImage;

        public ViewHolder(TextView text,TextView text2, ImageView image){
            this.textHolderSongTitle = text;
            this.textHolderAlbum = text2;
            this.holderImage = image;
        }
    }


}
