package com.test.obvious.viewModels

import android.app.Application
import android.util.Log
import androidx.annotation.WorkerThread
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
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel holds data (from API & database)
 * Used for both activities to load, cache, display image/data
 */
@Deprecated("Used to fetch mast 1 month data in one go if no picture-data found from DB; not exactly as per requirement")
class PicturesViewModel(application: Application) : AndroidViewModel(application) {

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

    private var dataLoadSize: Int = 0
    private var picturesLoaded = ArrayList<PictureResponse>()

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

    /**
     * Loads pictures from NASA api for last 1 month if no record found from DB
     */
    fun fetchLastMonthImages() {
        val dateList = ArrayList<String>()
        var calendar = Calendar.getInstance()
        val df = SimpleDateFormat(Constants.API.API_DATE_FORMAT)

        // last month
        calendar.add(Calendar.MONTH, -1)
        for (i in calendar.get(Calendar.DAY_OF_MONTH) until calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            calendar.set(Calendar.DATE, i)

            dateList.add(df.format(calendar.time))
            Log.d("Date" , df.format(calendar.time))
        }

        // current month
        calendar = Calendar.getInstance()
        for (i in 1 until calendar.get(Calendar.DAY_OF_MONTH)+1) {
            calendar.set(Calendar.DATE, i)

            dateList.add(df.format(calendar.time))
            Log.d("Date" , df.format(calendar.time))
        }

        // Data/picture loading size-number
        dataLoadSize = dateList.size

        // Fetch image
        for (temp in 1 until dateList.size) {
            fetchImage(dateList.get(index = temp))
        }
    }

    /**
     * Fetches image-data through api.
     * Since background operation , UI events are commented out
     */
    private fun fetchImage(date: String) {
        if (NetworkIoManager.isInternetAvailable(getApplication())) {

            NetworkIoManager.
                fetchImages(date).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onError = {
                    dataLoadSize--
                    loadDataFromDbIfAllPicturesFetched()
                    Log.e("Image Loading", "Couldn't load image")
                },
                    onSuccess = {
                        picturesLoaded.add(it)
                        dataLoadSize--
                        loadDataFromDbIfAllPicturesFetched()

                    })
                .addTo(disposable)

        } else {
            ioEvent.value = Constants.IOEvents.NO_NETWORK
        }
    }

    private fun loadDataFromDbIfAllPicturesFetched() {
        if (dataLoadSize <= 1) {
            if (picturesLoaded.size > 0) {
                GlobalScope.launch(Dispatchers.IO) {
                    val insertOperation = async { insertInDb() }
                    insertOperation.await()
                }
            }
            loadPictureData()
        }
    }

    /**
     * Performs DB operation to save data on worker-thread
     */
    @WorkerThread
    suspend fun insertInDb() {
        picturesDao.insertAll(picturesLoaded)
    }

}