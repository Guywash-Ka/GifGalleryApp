package com.bignerdranch.android.gifgalleryapp

import com.google.gson.annotations.SerializedName

class GifResponse {
    @SerializedName("data")
    lateinit var galleryItems: List<GalleryItem>
}