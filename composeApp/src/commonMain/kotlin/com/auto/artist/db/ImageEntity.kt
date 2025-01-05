package com.auto.artist.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "image_links")
@Serializable
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val prompt: String,
    val timestamp: Long
)
