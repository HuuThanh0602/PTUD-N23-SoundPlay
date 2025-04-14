package vn.edu.tlu.cse.soundplay.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import vn.edu.tlu.cse.soundplay.data.AppDatabase;
import vn.edu.tlu.cse.soundplay.data.dao.MusicDAO;
import vn.edu.tlu.cse.soundplay.data.api.ApiClient;
import vn.edu.tlu.cse.soundplay.data.api.ApiService;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.model.Playlist;

public class MusicRepository {

    private final ApiService apiService;
    private final Gson gson;
    private final OkHttpClient okHttpClient;
    private final MusicDAO musicDAO;

    public MusicRepository(Context context) {
        String BASE_URL = "https://www.phukienzzz.shop/api/";
        apiService = ApiClient.getClient(BASE_URL).create(ApiService.class);
        gson = new Gson();
        okHttpClient = new OkHttpClient();
        musicDAO = AppDatabase.getInstance(context).musicDAO();
    }

    // Các interface callback (giữ nguyên)
    public interface LyricCallback {
        void onSuccess(String lyric);
        void onError(String errorMessage);
    }

    public interface SearchCallback {
        void onSearchCompleted(List<Music> musics);
        void onSearchError(String error);
    }

    public interface Top100Callback {
        void onSuccess(List<Playlist> top100List);
        void onError(String errorMessage);
    }

    public interface NewReleaseCallback {
        void onSuccess(List<Music> newReleases);
        void onError(String errorMessage);
    }

    public interface PlayListCallback {
        void onSuccess(List<Music> playList);
        void onError(String errorMessage);
    }

    public interface RecentCallback {
        void onSuccess(List<Music> recentList);
        void onError(String errorMessage);
    }

    // Các phương thức API khác (giữ nguyên)
    public void getLyric(String id, final LyricCallback callback) {
        Call<Map<String, Object>> call = apiService.getLyric(id);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull retrofit2.Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> responseMap = response.body();
                        Object dataObj = responseMap.get("data");
                        String lyricUrl = null;
                        if (dataObj instanceof String) {
                            lyricUrl = (String) dataObj;
                        } else if (dataObj != null) {
                            lyricUrl = dataObj.toString();
                        }

                        if (lyricUrl != null && !lyricUrl.isEmpty()) {
                            Log.d("MusicRepository", "Lyric URL: " + lyricUrl);
                            Request request = new Request.Builder()
                                    .url(lyricUrl)
                                    .build();

                            okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                                    if (response.isSuccessful() && response.body() != null) {
                                        String lyricContent = response.body().string();
                                        callback.onSuccess(lyricContent);
                                    } else {
                                        callback.onError("Lỗi khi tải nội dung lời bài hát từ URL.");
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                                    callback.onError("Lỗi khi tải URL: " + e.getMessage());
                                }
                            });
                        } else {
                            callback.onError("URL lời bài hát không hợp lệ.");
                        }
                    } catch (Exception e) {
                        callback.onError("Lỗi khi phân tích dữ liệu: " + e.getMessage());
                    }
                } else {
                    callback.onError("Lỗi: Dữ liệu trả về không hợp lệ.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                callback.onError("Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }

    public void search(String keyword, final SearchCallback callback) {
        Call<Map<String, Object>> call = apiService.search(keyword);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull retrofit2.Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object dataObj = response.body().get("data");
                    String jsonArray = gson.toJson(dataObj);
                    Type listType = new TypeToken<List<Music>>() {}.getType();
                    List<Music> musics = gson.fromJson(jsonArray, listType);
                    callback.onSearchCompleted(musics);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                callback.onSearchError(t.getMessage());
            }
        });
    }

    public void getTop100(final Top100Callback callback) {
        Call<Map<String, Object>> call = apiService.getTop100();
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull retrofit2.Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object dataObj = response.body().get("data");
                    String jsonArray = gson.toJson(dataObj);
                    Type listType = new TypeToken<List<Playlist>>() {}.getType();
                    List<Playlist> top100List = gson.fromJson(jsonArray, listType);
                    callback.onSuccess(top100List);
                } else {
                    callback.onError("Lỗi: Dữ liệu trả về không hợp lệ.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getNewReleaseChart(final NewReleaseCallback callback) {
        Call<Map<String, Object>> call = apiService.getNewReleaseChart();
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull retrofit2.Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object dataObj = response.body().get("data");
                    String jsonArray = gson.toJson(dataObj);
                    Type listType = new TypeToken<List<Music>>() {}.getType();
                    List<Music> newReleaseList = gson.fromJson(jsonArray, listType);
                    callback.onSuccess(newReleaseList);
                } else {
                    callback.onError("Lỗi: Dữ liệu trả về không hợp lệ.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getPlayList(String id, final PlayListCallback callback) {
        Call<Map<String, Object>> call = apiService.getDetailPlaylist(id);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull retrofit2.Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object dataObj = response.body().get("data");
                    String jsonArray = gson.toJson(dataObj);
                    Type listType = new TypeToken<List<Music>>() {}.getType();
                    List<Music> playList = gson.fromJson(jsonArray, listType);
                    callback.onSuccess(playList);
                } else {
                    callback.onError("Lỗi: Dữ liệu trả về không hợp lệ.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getRecentPlays(RecentCallback callback) {
        new Thread(() -> {
            try {
                List<Music> recentList = musicDAO.getRecentPlays();
                if (recentList == null || recentList.isEmpty()) {
                    Log.d("MusicRepository", "No recent plays found in database, inserting sample data...");

                    // Tạo danh sách 10 bản ghi mẫu
                    List<Music> sampleMusicList = new ArrayList<>();
                    for (int i = 1; i <= 10; i++) {
                        Music music = new Music();
                        music.setId("sample_" + i);
                        music.setTitle("Sample Title " + i);
                        music.setThumbnail("https://example.com/thumb" + i + ".jpg");
                        music.setUrl("https://example.com/music" + i + ".mp3");
                        music.setArtist("Sample Artist " + i);
                        sampleMusicList.add(music);
                        musicDAO.insert(music);
                    }

                    // Lấy lại danh sách sau khi insert
                    recentList = musicDAO.getRecentPlays();
                    callback.onSuccess(recentList);
                } else {
                    Log.d("MusicRepository", "Recent plays loaded: " + recentList.size() + " items");
                    callback.onSuccess(recentList);
                }
            } catch (Exception e) {
                Log.e("MusicRepository", "Error fetching recent plays: " + e.getMessage());
                callback.onError("Error fetching recent plays: " + e.getMessage());
            }
        }).start();
    }


    // Phương thức để lưu bài hát vào danh sách gần đây
    public void saveRecentPlay(Music music) {
        new Thread(() -> {
            try {
                musicDAO.insert(music);
                Log.d("MusicRepository", "Saved recent play: " + music.getTitle());
            } catch (Exception e) {
                Log.e("MusicRepository", "Error saving recent play: " + e.getMessage());
            }
        }).start();
    }
}