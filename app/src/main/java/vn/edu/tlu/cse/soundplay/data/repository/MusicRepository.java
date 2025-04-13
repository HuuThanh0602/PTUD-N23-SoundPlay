package vn.edu.tlu.cse.soundplay.data.repository;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tlu.cse.soundplay.data.api.ApiClient;
import vn.edu.tlu.cse.soundplay.data.api.ApiService;
import vn.edu.tlu.cse.soundplay.data.model.Music;
import vn.edu.tlu.cse.soundplay.data.model.PlayList;

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
        void onSuccess(List<PlayList> top100List);
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
                    Type listType = new TypeToken<List<PlayList>>() {}.getType();
                    List<PlayList> top100List = gson.fromJson(jsonArray, listType);

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



}
