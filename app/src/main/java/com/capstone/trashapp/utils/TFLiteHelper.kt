package com.capstone.trashapp.utils

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteHelper(private val context: Activity) {
    private lateinit var labels: List<String>
    private lateinit var tflite: Interpreter

    private lateinit var inputImageBuffer: TensorImage
    private lateinit var outputProbabilityBuffer: TensorBuffer
    private lateinit var probabilityProcessor: TensorProcessor

    init {
        try {
            val opt = Interpreter.Options()
            tflite = Interpreter(loadModelFile("model-inception-sgd.tflite"), opt)
            labels = FileUtil.loadLabels(context, "waste.txt")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(modelFileName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    data class ClassificationResult(val label: String, val score: Float)

    fun classifyImage(bitmap: Bitmap): ClassificationResult? {
        val imageTensorIndex = 0
        val imageShape = tflite.getInputTensor(imageTensorIndex).shape() // {1, height, width, 3}
        val imageSizeY = imageShape[1]
        val imageSizeX = imageShape[2]
        val imageDataType = tflite.getInputTensor(imageTensorIndex).dataType()

        val probabilityTensorIndex = 0
        val probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape() // {1, NUM_CLASSES}
        val probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType()

        inputImageBuffer = TensorImage(imageDataType)
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType)
        probabilityProcessor = TensorProcessor.Builder().add(NormalizeOp(0f, 255f)).build()

        val cropSize = bitmap.width.coerceAtMost(bitmap.height)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(cropSize, cropSize))
            .add(ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(0f, 255f))
            .build()

        inputImageBuffer = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        tflite.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())

        val labeledProbability = TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
            .mapWithFloatValue

        val maxEntry = labeledProbability.maxByOrNull { it.value }

        return maxEntry?.let {
            ClassificationResult(it.key, it.value)
        }
    }
}