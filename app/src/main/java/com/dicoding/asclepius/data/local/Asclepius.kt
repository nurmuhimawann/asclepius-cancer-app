package com.dicoding.asclepius.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "asclepius")
data class Asclepius (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "result")
    var result: String? = null,

    @ColumnInfo(name = "confidenceScore")
    var confidenceScore: Float? = null,

    @ColumnInfo(name = "imageUri")
    var imageUri: String? = null,

    @ColumnInfo(name = "date")
    var date: String? = null
): Parcelable
