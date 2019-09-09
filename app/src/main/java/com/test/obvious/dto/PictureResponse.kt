package com.test.obvious.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.test.obvious.io.db.DbConstants

/** MODEL
 * Used for API response
 * DB entity
 */
@Entity(tableName = DbConstants.PICTURE_TABLE_NAME)
data class PictureResponse (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DbConstants.PICTURE_ID)
    val id: Int,

    @SerializedName("date")
    @ColumnInfo(name = DbConstants.DATE)
    val date: String? = "",

    @SerializedName("explanation")
    @ColumnInfo(name = DbConstants.EXPLANATION)
    val explanation: String? = "",

    @SerializedName("hdurl")
    @ColumnInfo(name = DbConstants.HD_URL)
    val hdUrl: String? = "",

    @SerializedName("media_type")
    @ColumnInfo(name = DbConstants.MEDIA_TYPE)
    val mediaType: String? = "",

    @SerializedName("service_version")
    @ColumnInfo(name = DbConstants.SERVICE_VERSION)
    val serviceVersion: String? = "",

    @SerializedName("title")
    @ColumnInfo(name = DbConstants.TITLE)
    val title: String? = "",

    @SerializedName("url")
    @ColumnInfo(name = DbConstants.URL)
    val url: String? = ""

)