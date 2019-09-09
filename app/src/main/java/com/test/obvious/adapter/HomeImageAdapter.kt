package com.test.obvious.adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.obvious.R
import com.test.obvious.dto.PictureResponse
import com.test.obvious.glide.setUrl
import kotlinx.android.synthetic.main.picture_adapter_item.view.*

class HomeImageAdapter(val context: Context,
                       val pictureList: List<PictureResponse?>? = null,
                       val pictureClickListener: (movie: PictureResponse?) -> Unit) : RecyclerView.Adapter<HomeImageAdapter.PictureHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
        return PictureHolder(LayoutInflater.from(context).inflate(R.layout.picture_adapter_item, parent, false))
    }

    override fun getItemCount(): Int { return pictureList?.size ?: 0 }

    override fun onBindViewHolder(holder: PictureHolder, position: Int) {
        val picture = pictureList?.get(position)

        picture?.let {
            holder.rootView.picture_adapter_title_textView.text = picture.title
            holder.rootView.picture_adapter_date_textView.text = picture.date
            if (!TextUtils.isEmpty(picture.url) && picture.url!!.contains("apod.nasa.gov")) {
                holder.rootView.picture_adapter_imgView.setUrl(picture.url)
            } else {
                Log.e("TAG", "Inappropriate link found")
            }
        }
    }

    /**
     * View holder class
     */
    inner class PictureHolder(var rootView: View) : RecyclerView.ViewHolder(rootView) {
        init {
            rootView.picture_adapter_imgView.setOnClickListener { pictureClickListener(pictureList?.get(adapterPosition)) }
        }
    }
}