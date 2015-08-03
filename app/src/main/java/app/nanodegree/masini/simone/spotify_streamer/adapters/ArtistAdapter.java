package app.nanodegree.masini.simone.spotify_streamer.adapters;

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
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Package: app.nanodegree.masini.simone.spotify_streamer
 * Project: Spotify-Streamer
 * Created by Simone Masini on 14/06/2015 at 10.55.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    private ActionBarActivity context;

    public ArtistAdapter(Context context){
        super(context, R.layout.artrist_row);
        this.context = (ActionBarActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if(convertView==null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.artrist_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (holder == null && convertView != null) {
            Object tag = convertView.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        Artist artist = getItem(position);
        if(artist!=null && holder !=null){
            holder.textHolderName.setText(artist.name);
            if(artist.images.size()>0 && !artist.images.get(artist.images.size()-1).url.equals(""))
                Picasso.with(context).load(artist.images.get(artist.images.size()-1).url).into(holder.holderImage);
        }
        return convertView;
    }

    public static class ViewHolder {
        public final TextView textHolderName;
        public final ImageView holderImage;

        public ViewHolder(View view){
            this.textHolderName = (TextView) view.findViewById(R.id.textview_artist_name);
            this.holderImage = (ImageView) view.findViewById(R.id.imageview_artist_thumbnail);
        }
    }
}
