package app.nanodegree.masini.simone.spotify_streamer;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
            TextView artistName = (TextView) convertView.findViewById(R.id.textview_artist_name);
            ImageView photo = (ImageView) convertView.findViewById(R.id.imageview_artist_thumbnail);
            holder = new ViewHolder(artistName, photo);
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
            if(artist.images.size()>0)
                Picasso.with(context).load(artist.images.get(0).url).into(holder.holderImage);

        }
        return convertView;
    }

    public static class ViewHolder {
        public final TextView textHolderName;
        public final ImageView holderImage;

        public ViewHolder(TextView text, ImageView image){
            this.textHolderName = text;
            this.holderImage = image;
        }
    }
}
