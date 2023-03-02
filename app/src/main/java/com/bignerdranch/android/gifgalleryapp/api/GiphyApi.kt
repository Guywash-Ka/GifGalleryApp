package com.bignerdranch.android.gifgalleryapp.api

import com.bignerdranch.android.gifgalleryapp.GifResponse
import retrofit2.Call
import retrofit2.http.GET

interface GiphyApi {
    @GET(
        "v1/gifs/trending?api_key=BpmerKwhpTD4Bl9PSpBaBVMtOYxK00Tf"
    )

    fun fetchGifs(): Call<GifResponse>
}