package vn.edu.tlu.cse.soundplay.data.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("playlist")
    Call<Map<String, Object>> getDetailPlaylist(@Query("id") String id);
    @GET("home")
    Call<Object> getHome();
    @GET("top100")
    Call<Map<String, Object>> getTop100();
    @GET("search")
    Call<Map<String, Object>> search(@Query("keyword") String keyword);

    @GET("new-release")
    Call<Map<String, Object>> getNewReleaseChart();

    @GET("lyric")
    Call<Map<String, Object>> getLyric(@Query("id") String id);

    @POST("register")
    Call<Map<String, Object>> register(@Body Map<String, String> request);

    @POST("verify-otp")
    Call<Map<String, Object>> verifyOtp(@Body Map<String, String> request);

    @POST("login")
    Call<Map<String, Object>> login(@Body Map<String, String> request);

    @POST("forgot-password")
    Call<Map<String, Object>> forgotPassword(@Body Map<String, String> request);

    @POST("reset-password")
    Call<Map<String, Object>> resetPassword(@Body Map<String, String> request);
    @POST("update-name")
    Call<Map<String, Object>> updateName(@Body Map<String, String> nameData);
}
