package com.auto.artist.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

//actual class DatabaseFactory(private val context: Context) {
//    actual fun create(): RoomDatabase.Builder<ImageDatabase> {
//        return Room.databaseBuilder(
//            context,
//            ImageDatabase::class.java,
//            ImageDatabase.DB_NAME
//        )
//    }
//}


actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<ImageDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(ImageDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
