/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend.api

import com.paysafe.example.BuildConfig
import com.paysafe.example.util.Consts
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {

    private const val HEADER_ACCEPT = "Accept"
    private const val HEADER_CONTENT_TYPE = "Content-Type"
    private const val HEADER_AUTHORIZATION = "Authorization"
    private const val HEADER_SIMULATOR = "Simulator"

    private val okHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        })
        .addInterceptor(Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            requestBuilder.run {
                addHeader(HEADER_ACCEPT, "application/json")
                addHeader(HEADER_CONTENT_TYPE, "application/json")
                addHeader(HEADER_SIMULATOR, "EXTERNAL")
                addHeader(HEADER_AUTHORIZATION, "Basic ${Consts.MERCHANT_BACKEND_API_KEY}")
            }

            return@Interceptor chain.proceed(requestBuilder.build())
        }).build()

    fun buildRetrofit() = Retrofit.Builder()
        .baseUrl(Consts.environment.url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}
