package com.example.tvoya_zhdulya.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CatApiService {
    @GET("cat?tags=orange,cute&json=true")
    Call<CatResponse> getRandomCat();
}