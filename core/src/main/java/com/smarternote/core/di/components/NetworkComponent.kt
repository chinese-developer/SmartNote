@file:Suppress("unused")

package com.smarternote.core.di.components

import android.content.Context
import com.smarternote.core.di.BaseAppComponent
import com.squareup.moshi.Moshi
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class NetworkComponent(
    private val context: Context,
    private val isDebugBuild: Boolean
) : BaseAppComponent() {
    private lateinit var retrofit: Retrofit
    private val interceptors = mutableListOf<Interceptor>()

    companion object {
        private const val DEFAULT_TIMEOUT = 20L
        private const val CONTENT_TYPE_JSON = "application/json"
    }

    override fun init() {
        createRetrofit()
    }

    fun updateBaseUrl(url: String) {
        createRetrofit(baseUrl = url)
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    fun addInterceptor(interceptor: Interceptor) {
        interceptors.add(interceptor)
        createRetrofit()
    }

    private fun createRetrofit(
        baseUrl: String = "https://api.example.com/",
        timeout: Long = DEFAULT_TIMEOUT
    ) {

        val logLevel = when (isDebugBuild) {
            true -> HttpLoggingInterceptor.Level.BODY
            else -> HttpLoggingInterceptor.Level.NONE
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = logLevel
        }

        val defaultHeadersInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .build()

            chain.proceed(newRequest)
        }

        val sslSocketFactory = createSSLSocketFactory()
        val trustManager = createX509TrustManager()


        val okHttpClientBuilder = OkHttpClient.Builder().apply {
            connectTimeout(timeout, TimeUnit.SECONDS)
            readTimeout(timeout, TimeUnit.SECONDS)
            writeTimeout(timeout, TimeUnit.SECONDS)

            addInterceptor(defaultHeadersInterceptor)

            if (sslSocketFactory != null && trustManager != null) {
                sslSocketFactory(sslSocketFactory, trustManager)
            }

            cookieJar(createCookieJar())
        }

        // 添加日志拦截器
        if (isDebugBuild) {
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        // 添加自定义拦截器
        interceptors.forEach {
            okHttpClientBuilder.addInterceptor(it)
        }

        val okHttpClient = okHttpClientBuilder.build()

        val moshi = Moshi.Builder().build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        // 根据需要实现您的SSL工厂创建逻辑
        return null
    }

    private fun createX509TrustManager(): X509TrustManager? {
        // 根据需要实现您的X509TrustManager创建逻辑
        return null
    }

    private fun createCookieJar(): CookieJar {
        // 根据需要实现您的CookieJar创建逻辑
        return CookieJar.NO_COOKIES
    }
}
