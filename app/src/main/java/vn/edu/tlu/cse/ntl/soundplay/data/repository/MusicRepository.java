package vn.edu.tlu.cse.ntl.soundplay.data.repository;



import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tlu.cse.ntl.soundplay.data.api.ApiClient;
import vn.edu.tlu.cse.ntl.soundplay.data.api.ApiService;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Music;
import vn.edu.tlu.cse.ntl.soundplay.data.model.Playlist;

public class MusicRepository {

    private final ApiService apiService;
    private final Gson gson;

    public MusicRepository() {
        String BASE_URL = "https://www.phukienzzz.shop/api/";
        apiService = ApiClient.getClient(BASE_URL).create(ApiService.class);
        gson = new Gson();
    }
    public interface SearchCallback {
        void onSearchCompleted(List<Music> musics);
        void onSearchError(String error);
    }
    public interface Top100Callback {
        void onSuccess(List<Playlist> top100List);
        void onError(String errorMessage);
    }

    public void search(String keyword, final SearchCallback callback) {
        Call<Map<String, Object>> call = apiService.search(keyword);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call,
                                   @NonNull Response<Map<String, Object>> response) {
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
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
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

    public interface NewReleaseCallback {
        void onSuccess(List<Music> newReleases);
        void onError(String errorMessage);
    }

    public void getNewReleaseChart(final NewReleaseCallback callback) {
        Call<Map<String, Object>> call = apiService.getNewReleaseChart();

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
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


    public void getRecentPlays(RecentCallback callback) {
        List<Music> recentList = new ArrayList<>();
        // Giả lập: lấy 3 bài gần đây
        recentList.add(new Music("Bắc Bling", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/e/3/3/9/e3399633d5289da5cb7c4292a41f0f67.jpg", "https://a128-z3.zmdcdn.me/2667eeae76859ab34357aff014bc0811?authen=exp=1744788228~acl=/2667eeae76859ab34357aff014bc0811*~hmac=ff3317358d1f99928bd7769920697f7b","Nhiều nghệ sĩ"));
        recentList.add(new Music("Như Ngọn Thủy Triều ", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/f/0/1/1/f01149f7efa5125f3120ace2d68c3140.jpg", "https://a128-z3.zmdcdn.me/3483908df9ffc10fbc7ed42db55c548e?authen=exp=1744788222~acl=/3483908df9ffc10fbc7ed42db55c548e*~hmac=1142bd66aca67ffe938e9c53af17152d","DICKSON"));
        recentList.add(new Music("Thủy Triều", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/8/4/7/4/8474eb9fd1a3aa78b974b4c104ff45fc.jpg", "https://a128-z3.zmdcdn.me/945f3ce83dd0eb820aa0e05cce267c5b?authen=exp=1744787976~acl=/945f3ce83dd0eb820aa0e05cce267c5b*~hmac=f422409c089f5b91af22c141a9c2151b","Quang Hùng MasterD"));

        callback.onSuccess(recentList);
    }

    public interface PlayListCallback {
        void onSuccess(List<Music> playList);
        void onError(String errorMessage);
    }

    public void getPlayList(String id,final PlayListCallback callback) {
        Call<Map<String, Object>> call = apiService.getDetailPlaylist(id);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
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

    public interface RecentCallback {
        void onSuccess(List<Music> recentList);
        void onError(String errorMessage);
    }


}