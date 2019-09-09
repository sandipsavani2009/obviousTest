package com.test.obvious.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.test.obvious.R
import com.test.obvious.adapter.HomeImageAdapter
import com.test.obvious.dto.PictureResponse
import com.test.obvious.io.server.retrofit.Constants
import com.test.obvious.viewModels.DateBasedPicturesViewModel
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : BaseActivity() {

    private lateinit var mViewModel : DateBasedPicturesViewModel

    override fun onDestroy() {
        mViewModel.dispose()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
    }

    /**
     * init ViewModel
     */
    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(DateBasedPicturesViewModel::class.java)
        mViewModel.ioEvent.observe(this, Observer { handleIoEvent(it) })
        mViewModel.allPictures.observe(this, Observer { onPictureDataUpdate(it) })
        mViewModel.fetchPictureForTodayIfRequires()
    }

    private fun handleIoEvent(it: Int?) {
        when(it) {
            Constants.IOEvents.NO_NETWORK -> showMessage("No Internet")
            else -> 0
        }
    }

    private fun onPictureDataUpdate(it: List<PictureResponse>?) {
        if (it != null && it.isNotEmpty()) {
            val sortedList = it.sortedWith(compareByDescending(PictureResponse::date))

            home_images_recyclerView.layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
            home_images_recyclerView.adapter = HomeImageAdapter(this, sortedList) { onPictureClicked(it) }

        } /*else {
            showMessage("Please wait, fetching data ...")
            mViewModel.fetchLastMonthImages()
        }*/
    }

    private fun onPictureClicked(it: PictureResponse?) {
        val intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra(Constants.BundleKeys.IMAGE_TAP_ID, it?.id)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, home_images_recyclerView, "picture")
        startActivity(intent, options.toBundle())
    }

    private fun showMessage(msg: String) {
        Snackbar.make(home_root_layout, msg, Snackbar.LENGTH_SHORT).show()
    }
}
