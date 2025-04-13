package vn.edu.tlu.cse.soundplay.ui;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.model.PlayList;
import vn.edu.tlu.cse.soundplay.data.repository.MusicRepository;

public class SearchActivity extends AppCompatActivity {

    private MusicRepository musicRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        musicRepository = new MusicRepository();

        String keyword = "em cua ngay hom qua";
        //searchMusic(keyword);

        getTop100Music();
    }

    private void searchMusic(String keyword) {
        musicRepository.search(keyword, new MusicRepository.SearchCallback() {
            @Override
            public void onSearchCompleted(List<Music> musics) {
                for (Music music : musics) {
                    Log.d("SearchResult", "ID: " + music.getId() + ", Tên bài hát: " + music.getTitle() + ", Ảnh: " + music.getThumbnail());
                }
            }

            @Override
            public void onSearchError(String error) {
                Log.e("SearchResult", "Lỗi: " + error);
            }
        });
    }

    private void getTop100Music() {
        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<PlayList> top100List) {
                for (PlayList playLists : top100List) {
                    Log.d("Top100", "ID: " + playLists.getId() + ", Tên bài hát: " + playLists.getTitle() + ", Ảnh: " + playLists.getThumbnail());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Top100", "Lỗi: " + errorMessage);
            }
        });
    }
}
