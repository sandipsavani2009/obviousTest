package com.test.obvious.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.test.obvious.ObviousApplication
import com.test.obvious.dto.PictureResponse
import com.test.obvious.io.db.ObviousRoomDatabase
import com.test.obvious.io.db.PicturesDao
import com.test.obvious.io.server.retrofit.Constants
import com.test.obvious.io.server.retrofit.NetworkIoManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DateBasedPicturesViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Resource holder disposable streams
     */
    private val disposable = CompositeDisposable()

    /**
     * Database Data-Access-Object to perform DB operations
     */
    private val picturesDao: PicturesDao = ObviousRoomDatabase.getDatabase(ObviousApplication.INSTANT, viewModelScope).picturesDao()

    /**
     * List of loaded data/picture info from API/DB
     */
    var allPictures = MutableLiveData<List<PictureResponse>>()

    /**
     * Other listenable IO events
     */
    var ioEvent = MutableLiveData<Int>()

    /**
     * Dispose data when no longer main component available
     */
    fun dispose() {
        disposable.dispose()
    }

    /**
     * Loads data from DB whenever ViewModel initializes
     */
    fun loadPictureData() {
        viewModelScope.launch {
            allPictures.value = withContext(Dispatchers.IO) { picturesDao.getAllPictureData()}
        }
    }

    fun fetchPictureForTodayIfRequires() {
        val format = SimpleDateFormat(Constants.API.API_DATE_FORMAT).format(Date())

        viewModelScope.launch {
            val pictureResponse = withContext(Dispatchers.IO) { picturesDao.getPictureRecordForDate(format) }
            if (pictureResponse == null) {
                fetchImage(format)
            } else {
                loadPictureData()
            }
        }
    }

    /**
     * Fetches image-data through api.
     * Since background operation , UI events are commented out
     */
    private fun fetchImage(date: String) {
        if (NetworkIoManager.isInternetAvailable(getApplication())) {

            NetworkIoManager.fetchImages(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onError = {
                    loadPictureData()
                },
                    onSuccess = {
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) { picturesDao.insert(it) }
                            loadPictureData()
                        }
                    })
                .addTo(disposable)

        } else {
            ioEvent.value = Constants.IOEvents.NO_NETWORK
        }
    }
}