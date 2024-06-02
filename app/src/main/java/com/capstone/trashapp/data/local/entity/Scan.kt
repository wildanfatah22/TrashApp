package com.capstone.trashapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "scans")
data class Scan(
    @field:PrimaryKey(autoGenerate = true) @field:ColumnInfo(name = "id") val id: Int = 0,
    @field:ColumnInfo(name = "label") val label: String,
    @field:ColumnInfo(name = "confidence_score") val confidenceScore: Float,
    @field:ColumnInfo(
        name = "scan_img_blob",
        typeAffinity = ColumnInfo.BLOB
    ) val scanImgBlob: ByteArray,
    @field:ColumnInfo(name = "timestamp")
    val timestamp: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Scan
        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}