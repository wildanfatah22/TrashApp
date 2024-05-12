package com.capstone.trashapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File

fun imageFileToByteArray(imageFile: File): ByteArray {
    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun ImageView.renderBlob(byteArray: ByteArray) {
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    this.setImageBitmap(bitmap)
}