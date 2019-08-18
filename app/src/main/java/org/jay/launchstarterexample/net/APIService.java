package org.jay.launchstarterexample.net;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("news/item")
    Call<ResponseBody> getNBANews(@Query("column") String column,
                                  @Query("articleIds") String articleIds);

}