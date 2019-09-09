package com.test.indihoodloantest.io.retrofit

import com.test.obvious.dto.PictureResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API endpoints
 */
interface RetroService {

    @GET("apod")
    fun fetchImages(@Query("api_key") apiKey: String,
                    @Query("date") date: String): Single<PictureResponse>
}