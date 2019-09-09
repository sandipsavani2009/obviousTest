package com.test.obvious.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.test.obvious.R
import com.test.obvious.adapter.ImageViewPagerAdapter
import com.test.obvious.dto.PictureResponse
import com.test.obvious.io.server.retrofit.Constants
import com.test.obvious.viewModels.DateBasedPicturesViewModel
import com.test.obvious.viewModels.PicturesViewModel
import kotlinx.android.synthetic.main.activity_image_viewer.*
import kotlinx.android.synthetic.main.activity_main.*

class ImageViewerActivity : BaseActivity() {

    private lateinit var mViewModel : DateBasedPicturesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        initViewModel()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(DateBasedPicturesViewModel::class.java)
        mViewModel.allPictures.observe(this, Observer { populateViewPagerAdapter(it) })
        mViewModel.loadPictureData()
    }

    private fun populateViewPagerAdapter(allPictures: List<PictureResponse?>?) {
        allPictures?.let {
            imageViewPager.adapter = ImageViewPagerAdapter(this@ImageViewerActivity, allPictures)
            imageViewPager.offscreenPageLimit = allPictures.size
            setupViewingPosition()
            showMessage("Showing Cached Images")
        }
    }

    private fun setupViewingPosition() {
        intent?.let {
            val tapPos = intent.getIntExtra(Constants.BundleKeys.IMAGE_TAP_ID, -1)
            if (tapPos > 0) {
                imageViewPager.currentItem = (imageViewPager.adapter as ImageViewPagerAdapter).getPagePosition(tapPos)
            }
        }
    }

    private fun showMessage(msg: String) {
        Snackbar.make(root_layout, msg, Snackbar.LENGTH_SHORT).show()
    }

}
