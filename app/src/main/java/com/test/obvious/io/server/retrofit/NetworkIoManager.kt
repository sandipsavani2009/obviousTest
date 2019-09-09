package com.test.obvious.io.server.retrofit

import android.content.Context
import android.net.ConnectivityManager
import com.test.indihoodloantest.io.retrofit.RetroService
import com.test.obvious.dto.PictureResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Manager handles all network operations like, fetching data using APIs
 */
object NetworkIoManager {

    private var service: RetroService? = null

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/planetary/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getHttpClient())
            .build()

        service = retrofit!!.create(RetroService::class.java)
    }

    private fun getHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        httpClient.readTimeout(30, TimeUnit.SECONDS)
        httpClient.addNetworkInterceptor(logging)

        return httpClient.build()
    }

    fun fetchImages(date: String): Single<PictureResponse> {
        return service!!.fetchImages(Constants.API.KEY, date)
    }


    fun isInternetAvailable (context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return  networkInfo != null && networkInfo.isConnected
    }
}