package com.mericoztiryaki.yasin.core.network;

import com.google.gson.GsonBuilder;
import com.mericoztiryaki.yasin.Constants;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by as on 2019-05-08.
 */
public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = Constants.BASE_URL;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(getConverterFactory())
                    .client(getOkHttpClient())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cache(null)
                .build();
    }

    private static GsonConverterFactory getConverterFactory() {
        return GsonConverterFactory.create(
                new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create());
    }

}
