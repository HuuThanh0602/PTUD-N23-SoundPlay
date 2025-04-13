package vn.edu.tlu.cse.hyn.soundplay.data.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "favourites")
public class Favourite {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String musicId;
    private String title;
    private String artist;
    private String thumbnail;

    public Favourite(String musicId, String title, String artist, String thumbnail) {
        this.musicId = musicId;
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
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
}
