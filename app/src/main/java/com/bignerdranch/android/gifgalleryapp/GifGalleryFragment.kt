package com.bignerdranch.android.gifgalleryapp

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Photo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.gifgalleryapp.api.GiphyApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "GifGalleryFragment"

class GifGalleryFragment: Fragment() {

    private lateinit var gifGalleryViewModel: GifGalleryViewModel
    private lateinit var gifRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gifGalleryViewModel = ViewModelProviders.of(this).get(GifGalleryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    private class GifHolder(itemTextView: TextView): RecyclerView.ViewHolder(itemTextView) {
        val bindTitle: (CharSequence) -> Unit = itemTextView::setText
    }

    private class GifAdapter(private val galleryItems:  List<GalleryItem>): RecyclerView.Adapter<GifHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): GifHolder {
            val textView = TextView(parent.context)
            return GifHolder(textView)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: GifHolder, position: Int) {
            val galleryItem = galleryItems[position]
            holder.bindTitle(galleryItem.title)
        }
    }

    companion object {
        fun newInstance() = GifGalleryFragment()
    }

}