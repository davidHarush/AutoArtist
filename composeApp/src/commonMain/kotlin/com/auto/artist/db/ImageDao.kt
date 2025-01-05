package com.auto.artist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageLink(imageLink: ImageEntity)

    @Query("SELECT * FROM image_links")
    suspend fun getAllImages(): List<ImageEntity>

    @Query("DELETE FROM image_links WHERE id = :id")
    suspend fun deleteImage(id: Int)
}
