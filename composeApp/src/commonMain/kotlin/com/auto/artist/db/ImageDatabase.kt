package com.auto.artist.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImageEntity::class], version = 1)


@ConstructedBy(ImageDatabaseConstructor::class)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        const val DB_NAME = "image.db"
    }
}
