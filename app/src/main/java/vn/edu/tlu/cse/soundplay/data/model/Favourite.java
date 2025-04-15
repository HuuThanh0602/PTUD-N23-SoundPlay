package vn.edu.tlu.cse.soundplay.data.model;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favourites")
public class Favourite {
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    private String title;
    private String artist;
    private String thumbnail;

    private String url;

    public Favourite() {

    }

    public Favourite(String title, String artist, String thumbnail, String url) {
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
        this.url = url;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
