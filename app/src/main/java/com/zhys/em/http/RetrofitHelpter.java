package com.zhys.em.http;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelpter {

    private static final String BASE_URL = "http://v.juhe.cn";

    private static OkHttpClient.Builder sOkHttpBuilder;

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;

    public static <K> K createApi(Class<K> cls) {
        return getRetrofit().create(cls);
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {

            synchronized (RetrofitHelpter.class) {

                if (retrofit == null) {
                    sOkHttpBuilder = new OkHttpClient.Builder();
                    okHttpClient = sOkHttpBuilder.build();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }

            }

        }
        return retrofit;
    }

}
