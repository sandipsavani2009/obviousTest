package com.test.obvious.io.db

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.obvious.dto.PictureResponse

/**
 * Data access Object - to pass queries of DB operations
 */
@Dao
abstract class PicturesDao {

    @WorkerThread
    @Query("SELECT * from ${DbConstants.PICTURE_TABLE_NAME}")
    abstract suspend fun getAllPictureData(): List<PictureResponse>?

    @WorkerThread
    @Query("SELECT * From ${DbConstants.PICTURE_TABLE_NAME} WHERE ${DbConstants.DATE} = :todayDate")
    abstract suspend fun getPictureRecordForDate(todayDate: String): PictureResponse?

    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(picture: PictureResponse)

    //-------- Used for PictureViewModel (Unnecessary) ------
    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(pictures: List<PictureResponse>)

    @WorkerThread
    @Query("DELETE FROM ${DbConstants.PICTURE_TABLE_NAME} WHERE ${DbConstants.PICTURE_ID} = :id" )
    abstract suspend fun delete(id: String)
}