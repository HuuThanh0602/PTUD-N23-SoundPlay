package vn.edu.tlu.cse.soundplay.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.tlu.cse.soundplay.data.AppDatabase;
import vn.edu.tlu.cse.soundplay.data.dao.FavouriteDAO;
import vn.edu.tlu.cse.soundplay.data.model.Favourite;

public class FavouriteRepository extends AndroidViewModel {

    private final FavouriteDAO favouriteDao;
    private final LiveData<List<Favourite>> allFavourites;
    private final ExecutorService executorService;

    public FavouriteRepository(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        favouriteDao = db.favouriteDAO();
        allFavourites = favouriteDao.getAllFavouritesLive();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Favourite>> getAllFavourites() {
        return allFavourites;
    }

    public void insert(Favourite favourite) {
        executorService.execute(() -> favouriteDao.insert(favourite));
    }

    public void delete(Favourite favourite) {
        executorService.execute(() -> favouriteDao.delete(favourite));
    }

    public void deleteById(String id) { // Đổi từ deleteByMusicId
        executorService.execute(() -> {
            Favourite existing = favouriteDao.findById(id); // Sửa thành findById
            if (existing != null) {
                favouriteDao.delete(existing);
            }
        });
    }

    public void toggleFavourite(Favourite favourite) {
        executorService.execute(() -> {
            Favourite existing = favouriteDao.findById(favourite.getId()); // Sửa thành findById
            if (existing != null) {
                favouriteDao.delete(existing);
            } else {
                favouriteDao.insert(favourite);
            }
        });
    }

    public Favourite findByIdSync(String id) { // Đổi từ findByMusicIdSync
        return favouriteDao.findById(id); // Sửa thành findById
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

}