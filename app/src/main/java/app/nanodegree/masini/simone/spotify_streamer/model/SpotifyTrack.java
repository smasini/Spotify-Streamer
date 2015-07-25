package app.nanodegree.masini.simone.spotify_streamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 20/07/2015.
 */
public class SpotifyTrack implements Parcelable {

    private String id;
    private String name;
    private String artistName;
    private String albumName;
    private String urlImage;
    private String previewUrl;

    public SpotifyTrack(){}

    public SpotifyTrack(Parcel in){
        String[] data = new String[6];
        in.readStringArray(data);

        this.id = data[0];
        this.name = data[1];
        this.artistName = data[2];
        this.albumName = data[3];
        this.urlImage = data[4];
        this.previewUrl = data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                this.id,
                this.name,
                this.artistName,
                this.albumName,
                this.urlImage,
                this.previewUrl
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SpotifyTrack createFromParcel(Parcel in) {
            return new SpotifyTrack(in);
        }

        public SpotifyTrack[] newArray(int size) {
            return new SpotifyTrack[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
