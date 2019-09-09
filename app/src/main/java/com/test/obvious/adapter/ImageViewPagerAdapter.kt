package com.test.obvious.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.test.obvious.R
import com.test.obvious.dto.PictureResponse
import kotlinx.android.synthetic.main.image_viewer_viewpager_item.view.*

/**
 * View pager adapter showing full image as swipeable
 */
class ImageViewPagerAdapter(private val context: Context,
                            private val pictureList: List<PictureResponse?>?) : PagerAdapter() {

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any as RelativeLayout
    }

    override fun getCount(): Int {
        return pictureList?.size ?: 0
    }

    /**
     * get position of page/adapter item from list
     */
    fun getPagePosition(id: Int): Int {
        pictureList?.let {
            for (pos in pictureList.indices) {
                if (pictureList[pos]?.id == id) {
                    return pos
                }
            }
        }
        return 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewLayout = inflater.inflate(R.layout.image_viewer_viewpager_item, container, false)

        if (pictureList != null && pictureList[position] != null) {
            val picture = pictureList[position]

            val requestOptions = RequestOptions().
                onlyRetrieveFromCache(true).
                placeholder(R.drawable.loading)

            Glide.with(context).
                load(picture?.url).
                apply(requestOptions).
                into(viewLayout.imgDisplay)

            viewLayout.img_title_textView.text = picture?.title
            viewLayout.img_date_textView.text = picture?.date
            viewLayout.img_explanation_textView.text = picture?.explanation

            (container as ViewPager).addView(viewLayout)
        }

        return viewLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        (container as ViewPager).removeView(any as RelativeLayout)
    }
}