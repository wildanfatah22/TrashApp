package com.capstone.trashapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import com.capstone.trashapp.ml.ModelInceptionSgd
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ImageClassifierHelper(context: Context) {

    companion object {
        private const val TAG = "ImageClassifierHelper"
        private const val imageSize = 224
    }

    private val model: ModelInceptionSgd = ModelInceptionSgd.newInstance(context)
    private val classes = arrayOf("Cardboard", "Glass", "Metal", "Organic", "Paper", "Plastic")

    fun close() {
        model.close()
    }

    fun classifyImage(image: Bitmap): ClassificationResult {
        // Resize image to the required input size of the model
        var dimension = Math.min(image.width, image.height)
        var resizedImage = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
        resizedImage = Bitmap.createScaledBitmap(resizedImage, imageSize, imageSize, false)

        // Convert Bitmap to ByteBuffer
        val byteBuffer = convertBitmapToByteBuffer(resizedImage)

        // Create TensorBuffer for input
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Run inference
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Process the output to get class and confidence
        val confidences = outputFeature0.floatArray
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }

        // Return the result
        val className = classes[maxPos]
        val confidence = confidences[maxPos]
        return ClassificationResult(className, confidence)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(imageSize * imageSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value = intValues[pixel++] // RGB
                byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255f))
                byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255f))
                byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
            }
        }

        return byteBuffer
    }

    data class ClassificationResult(val className: String, val confidence: Float)
}

