package com.bignerdranch.android.gifgalleryapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class GifGalleryViewModel: ViewModel() {

    val galleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        galleryItemLiveData = GiphyFetchr().fetchGifs()
    }

}