package vn.edu.tlu.cse.soundplay.data.repository;


import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.edu.tlu.cse.soundplay.data.api.ApiService;

public class AuthRepository {
    private final ApiService apiService;

    public AuthRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.phukienzzz.shop/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public Call<Map<String, Object>> register(String name, String email, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("name", name);
        request.put("email", email);
        request.put("password", password);
        request.put("password_confirmation", password);

        return apiService.register(request);
    }

    public Call<Map<String, Object>> verifyOtp(String email, String otp) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("otp", otp);

        return apiService.verifyOtp(request);
    }

    public Call<Map<String, Object>> login(String email, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);

        return apiService.login(request);
    }

    public Call<Map<String, Object>> forgotPassword(String email) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        return apiService.forgotPassword(request);
    }

    public Call<Map<String, Object>> resetPassword(String email, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);
        request.put("password_confirmation", password);

        return apiService.resetPassword(request);
    }
    public Call<Map<String, Object>> updateName(String name, String email) {
        Map<String, String> nameData = new HashMap<>();
        nameData.put("name", name);
        nameData.put("email", email);

        return apiService.updateName(nameData);
    }

}