package vn.edu.tlu.cse.ntl.soundplay.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_hository")
public class SearchHository {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String key_word;

    public SearchHository(String key_word) {
        this.key_word = key_word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey_word() {
        return key_word;
    }

    public void setKey_word(String key_word) {
        this.key_word = key_word;
    }
}