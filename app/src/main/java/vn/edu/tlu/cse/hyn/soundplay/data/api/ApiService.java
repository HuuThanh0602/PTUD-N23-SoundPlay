package vn.edu.tlu.cse.hyn.soundplay.data.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("song")
    Call<Object> getSong();
    @GET("playlist")
    Call<Object> getDetailPlaylist();
    @GET("home")
    Call<Object> getHome();
    @GET("top100")
    Call<Map<String, Object>> getTop100();
    @GET("search")
    Call<Map<String, Object>> search(@Query("keyword") String keyword);
    @GET("chart")
    Call<Object> getChartHome();
    @GET("new-release")
    Call<Object> getNewReleaseChart();
    @GET("info")
    Call<Object> getInfoSong(@Query("id") String id);
    @GET("artist")
    Call<Object> getArtist(@Query("name") String name);
    @GET("artist-song")
    Call<Object> getListArtistSong(@Query("id") String id);
    @GET("lyric")
    Call<Object> getLyric(@Query("id") String id);

    @GET("mv")
    Call<Object> getListMV();
    @GET("category-mv")
    Call<Object> getCategoryMV();
    @GET("video")
    Call<Object> getVideo(@Query("id") String id);
}
