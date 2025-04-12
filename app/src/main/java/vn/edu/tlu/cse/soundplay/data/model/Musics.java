package vn.edu.tlu.cse.soundplay.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "musics")
public class Musics {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String id_music;
    private String name;
    private String image;
    private String url;
    private String lyric;

    public Musics(String id_music, String name, String image, String url, String lyric) {
        this.id_music = id_music;
        this.name = name;
        this.image = image;
        this.url = url;
        this.lyric = lyric;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_music() {
        return id_music;
    }

    public void setId_music(String id_music) {
        this.id_music = id_music;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
}
