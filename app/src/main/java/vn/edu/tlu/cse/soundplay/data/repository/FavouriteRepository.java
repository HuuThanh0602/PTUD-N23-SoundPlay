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

    public void toggleFavourite(Favourite favourite) {
        executorService.execute(() -> {
            Favourite existing = favouriteDao.findById(favourite.getId());
            if (existing != null) {
                favouriteDao.delete(existing);
            } else {
                favouriteDao.insert(favourite);
            }
        });
    }

    public Favourite findByIdSync(String id) {
        return favouriteDao.findById(id);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

}