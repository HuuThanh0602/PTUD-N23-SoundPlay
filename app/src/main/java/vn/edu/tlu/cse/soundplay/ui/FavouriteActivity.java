package vn.edu.tlu.cse.soundplay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tlu.cse.soundplay.R;
import vn.edu.tlu.cse.soundplay.adapter.FavouriteAdapter;
import vn.edu.tlu.cse.soundplay.data.repository.FavouriteRepository;

public class FavouriteActivity extends BaseActivity {

    private RecyclerView rvFavourites;
    private FavouriteAdapter adapter;
    private FavouriteRepository viewModel;

    private ImageView iconHome, iconProfile, iconLibrary,iconSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_favourite); // Đảm bảo layout có tên đúng!

        initMiniPlayer();

        // 1. Ánh xạ RecyclerView
        rvFavourites = findViewById(R.id.rvCategories);
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));

        // 2. Khởi tạo ViewModel và Adapter
        viewModel = new ViewModelProvider(this).get(FavouriteRepository.class);
        adapter = new FavouriteAdapter(this, new ArrayList<>(), viewModel);
        rvFavourites.setAdapter(adapter);

        // 3. Quan sát LiveData
        viewModel.getAllFavourites().observe(this, adapter::updateData);

        // 4. Xử lý Bottom Navigation
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        iconHome = findViewById(R.id.home);
        iconLibrary = findViewById(R.id.library);
        iconProfile = findViewById(R.id.profile);
        iconSearch=findViewById(R.id.search);

        iconLibrary.setOnClickListener(v -> {
            Intent intent = new Intent(FavouriteActivity.this, LibraryActivity.class);
            startActivity(intent);
            finish(); // kết thúc trang hiện tại để tránh back về
        });
        iconSearch.setOnClickListener(v -> {
            Intent intent = new Intent(FavouriteActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        });
        iconHome.setOnClickListener(v -> {
            Intent intent = new Intent(FavouriteActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Hồ sơ
        iconProfile.setOnClickListener(v -> {
            Intent intent = new Intent(FavouriteActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });


    }
}
