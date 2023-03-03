package com.bignerdranch.android.gifgalleryapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.gifgalleryapp.api.GiphyApi
import com.bignerdranch.android.gifgalleryapp.api.GiphyResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "GiphyFetchr"

class GiphyFetchr {
    private val giphyApi: GiphyApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.giphy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        giphyApi = retrofit.create(GiphyApi::class.java)
    }

    fun fetchGifs(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val giphyRequest: Call<GifResponse> = giphyApi.fetchGifs()

        giphyRequest.enqueue(object : Callback<GifResponse> {
            override fun onFailure(call: Call<GifResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch gifs", t)
            }
            override fun onResponse(
                call: Call<GifResponse>,
                response: Response<GifResponse>
            ) {
                Log.d(TAG, "Response received")
//                val giphyResponse: GiphyResponse? = response.body()
//                val gifResponse: GifResponse? = giphyResponse?.data
                val gifResponse: GifResponse? = response.body()
                var galleryItems: List<GalleryItem> = gifResponse?.galleryItems?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }

    @WorkerThread
    fun fetchGif(url: String): Bitmap? {
        val response: Response<ResponseBody> = giphyApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Response body: ${response.body()?.byteStream()} bitmap=$bitmap from Response=$response")
        return bitmap

    }

}