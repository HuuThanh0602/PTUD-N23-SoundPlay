package vn.edu.tlu.cse.hyn.soundplay.data.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.tlu.cse.hyn.soundplay.data.dao.AppDatabase;
import vn.edu.tlu.cse.hyn.soundplay.data.dao.favouriteDAO;
import vn.edu.tlu.cse.hyn.soundplay.data.model.Favourite;

public class FavouriteRepository extends AndroidViewModel {

    private final favouriteDAO favouriteDao;
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

    public void deleteByMusicId(String musicId) {
        executorService.execute(() -> {
            Favourite existing = favouriteDao.findByMusicId(musicId);
            if (existing != null) {
                favouriteDao.delete(existing);
            }
        });
    }

    public void toggleFavourite(Favourite favourite) {
        executorService.execute(() -> {
            Favourite existing = favouriteDao.findByMusicId(favourite.getMusicId());
            if (existing != null) {
                favouriteDao.delete(existing);
            } else {
                favouriteDao.insert(favourite);
            }
        });
    }

    public Favourite findByMusicIdSync(String musicId) {
        return favouriteDao.findByMusicId(musicId);
    }
}
