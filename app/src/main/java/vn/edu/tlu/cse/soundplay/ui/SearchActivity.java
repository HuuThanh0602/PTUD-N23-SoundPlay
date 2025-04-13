package vn.edu.tlu.cse.soundplay.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;

public class SearchActivity extends AppCompatActivity {

    private MusicRepository musicRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

       musicRepository = new MusicRepository();
        String keyword = "em cua ngay hom qua";
        searchMusic(keyword);

    }

    private void searchMusic(String keyword) {
        musicRepository.search(keyword, new MusicRepository.SearchCallback() {
            @Override
            public void onSearchCompleted(List<Music> musics) {
                for (Music music : musics) {
                    Log.d("MusicData", "ID: " + music.getId() + ", Tên bài hát: " + music.getTitle()+ ", Ảnh: " + music.getThumbnail());
                }
            }
            @Override
            public void onSearchError(String error) {
                Log.e("MusicData", "Lỗi: " + error);
            }
        });
    }
}
