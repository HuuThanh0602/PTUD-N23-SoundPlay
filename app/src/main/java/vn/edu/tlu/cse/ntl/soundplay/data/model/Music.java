package vn.edu.tlu.cse.ntl.soundplay.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "musics")
public class Music {
    @SerializedName("id")
    private String id;

    private String title;
    private String thumbnail;
    private String url;
    private String artist;

    public Music(String title, String thumbnail, String url, String artist) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.url = url;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}