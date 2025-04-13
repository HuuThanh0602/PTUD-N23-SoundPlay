package vn.edu.tlu.cse.soundplay.data.model;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;


@Entity(tableName = "play_list")
public class Playlist {
    @SerializedName("id")
    private String id;

    private String title;
    private String thumbnail;
    private String url;

    public Playlist(String title, String thumbnail, String url) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.url = url;
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
}
