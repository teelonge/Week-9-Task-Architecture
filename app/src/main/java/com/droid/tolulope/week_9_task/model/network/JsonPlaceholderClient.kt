package com.droid.tolulope.week_9_task.model.network

import com.droid.tolulope.week_9_task.utils.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * An object class that builds the retrofit service used to access the
 * JsonPlaceholderEndpoint, also includes an logging interceptor which logs
 * request and response information and uses GsonConverterFactory
 */

object JsonPlaceholderClient {

    fun getPlaceholderEndPoint() : JsonPlaceholderEndpoint{

        // HttpLogging
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        // Creates an implementation of the JsonPlaceholderEndpoint
        return Retrofit.Builder()
                .client(OkHttpClient.Builder().addInterceptor(logging).build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(JsonPlaceholderEndpoint::class.java)
    }
}