package vn.edu.tlu.cse.ntl.soundplay.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tlu.cse.ntl.soundplay.R;
import vn.edu.tlu.cse.ntl.soundplay.adapter.LastestAdapter;
import vn.edu.tlu.cse.ntl.soundplay.adapter.MusicAdapter;
import vn.edu.tlu.cse.ntl.soundplay.adapter.YouAdapter;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Music;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;
import vn.edu.tlu.cse.ntl.soundplay.data.repository.MusicRepository;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerLastest, recyclerForYou, recyclerRecentPlays;
    CircleImageView avatarHome;
    TextView txtUserName;
    private MusicRepository musicRepository;

    private ImageView searchIcon, libraryIcon, profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        musicRepository = new MusicRepository();


        searchIcon = findViewById(R.id.search);
        libraryIcon = findViewById(R.id.library);
        profileIcon = findViewById(R.id.profile);
        recyclerLastest = findViewById(R.id.recyclerLastest);
        recyclerForYou = findViewById(R.id.recyclerForYou);
        recyclerRecentPlays = findViewById(R.id.recyclerRecentPlays);
        avatarHome = findViewById(R.id.avatarHome);
        txtUserName = findViewById(R.id.txtUserName);

        loadUserProfile();

        recyclerForYou.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        getTop100Music();

        recyclerLastest.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.HORIZONTAL, false));
        getNewReleaseMusic();

        recyclerRecentPlays.setLayoutManager(new LinearLayoutManager(this));
        getRecentPlays();


        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            finish();
        });

        libraryIcon.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, LibraryActivity.class));
            finish();
        });
    }

    private void getTop100Music() {
        musicRepository.getTop100(new MusicRepository.Top100Callback() {
            @Override
            public void onSuccess(List<Playlist> top100List) {
                for (Playlist playList : top100List) {
                    Log.d("Top100", "ID: " + playList.getId() + ", Tên: " + playList.getTitle() + ", Ảnh: " + playList.getThumbnail());
                }
                YouAdapter youAdapter = new YouAdapter(top100List);
                recyclerForYou.setAdapter(youAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Top100", "Lỗi: " + errorMessage);
            }
        });
    }

    private void getNewReleaseMusic() {
        musicRepository.getNewReleaseChart(new MusicRepository.NewReleaseCallback() {
            @Override
            public void onSuccess(List<Music> newReleases) {
                for (Music music : newReleases) {
                    Log.d("NewRelease", "ID: " + music.getId() + ", Tên bài hát: " + music.getTitle() + ", Ảnh: " + music.getThumbnail());
                }
                LastestAdapter lastestAdapter = new LastestAdapter(newReleases);
                recyclerLastest.setAdapter(lastestAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("NewRelease", "Lỗi: " + errorMessage);
            }
        });
    }


    private void getRecentPlays() {
        musicRepository.getRecentPlays(new MusicRepository.RecentCallback() {
            @Override
            public void onSuccess(List<Music> musicList) {
                MusicAdapter adapter = new MusicAdapter(musicList);
                recyclerRecentPlays.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("RecentPlays", "Lỗi: " + errorMessage);
            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_profile", MODE_PRIVATE);
        String name = sharedPreferences.getString("KEY_NAME", "Hello!");
        String avatarBase64 = sharedPreferences.getString("KEY_AVATAR", "");

        txtUserName.setText(name);

        if (!avatarBase64.isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                avatarHome.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            avatarHome.setImageResource(R.drawable.ic_avatar);
        }
    }
}