package com.auto.artist.db

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ImageDatabaseConstructor : RoomDatabaseConstructor<ImageDatabase> {
    override fun initialize(): ImageDatabase
}