package com.bignerdranch.android.gifgalleryapp

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract.CommonDataKinds.Photo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bignerdranch.android.gifgalleryapp.api.GiphyApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "GifGalleryFragment"

class GifGalleryFragment: Fragment() {

    private lateinit var gifGalleryViewModel: GifGalleryViewModel
    private lateinit var gifRecyclerView: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<GifHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        gifGalleryViewModel =
            ViewModelProviders.of(this).get(GifGalleryViewModel::class.java)

        val responseHandler = Handler()
        thumbnailDownloader =
            ThumbnailDownloader(responseHandler) { gifHolder, bitmap ->
                val drawable = BitmapDrawable(resources, bitmap)
                gifHolder.bindDrawable(drawable)

            }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
        val view = inflater.inflate(R.layout.fragment_gif_gallery, container, false)

        gifRecyclerView = view.findViewById(R.id.gif_recycler_view)
        gifRecyclerView.layoutManager = GridLayoutManager(context, 3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gifGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer { galleryItems ->
                gifRecyclerView.adapter = GifAdapter(galleryItems)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    private class GifHolder(private val itemImageView: ImageView): RecyclerView.ViewHolder(itemImageView) {
        var img_android: ImageView

        init {
            img_android =
                itemImageView.findViewById<View>(R.id.iv_glide) as ImageView
        }
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
    }

    private inner class GifAdapter(private val galleryItems:  List<GalleryItem>): RecyclerView.Adapter<GifHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): GifHolder {
            val view = layoutInflater.inflate(
                R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return GifHolder(view)
        }

        override fun getItemCount(): Int = galleryItems.size

//        override fun onBindViewHolder(holder: GifHolder, position: Int) {
        override fun onBindViewHolder(holder: GifHolder, position: Int) {
            val galleryItem = galleryItems[position]
            val placeholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.giphy
            ) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
//            Glide.with(requireContext())
//                .load(R.drawable.giphy)
//                .into()
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(FitCenter(), RoundedCorners(16))
            Glide.with(context!!)
                .load(R.drawable.giphy)
                .apply(requestOptions)
                .skipMemoryCache(true)
                .into(holder.img_android)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var img_android: ImageView

        init {
            img_android =
                view.findViewById<View>(R.id.iv_glide) as ImageView
        }
    }

    companion object {
        fun newInstance() = GifGalleryFragment()
    }

}