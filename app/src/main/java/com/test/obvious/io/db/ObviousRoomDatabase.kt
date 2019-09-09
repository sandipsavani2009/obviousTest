package com.test.obvious.io.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.test.obvious.dto.PictureResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Room Databse
 */
@Database(entities = [PictureResponse::class], version = 1, exportSchema = false)
public abstract class ObviousRoomDatabase : RoomDatabase() {

    abstract fun picturesDao(): PicturesDao

    companion object {
        private var INSTANCE: ObviousRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ObviousRoomDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ObviousRoomDatabase::class.java,
                        DbConstants.DATABASE_NAME
                    ).addCallback(DatabaseCallback(scope)).build()
                }
            }
            return INSTANCE!!
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }

    }


}