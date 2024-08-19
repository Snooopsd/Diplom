package com.example.diplom

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import java.io.IOException
import java.nio.MappedByteBuffer


class TFLmodel (private val context: Context) {

    private lateinit var interpreter: Interpreter
    private val modelName: String = "model/converted_model.tflite"
    private val imageSize: Int // Размер изображения, ожидаемый моделью

    init {
        val options = Interpreter.Options()
        var nnApiDelegate: NnApiDelegate? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            nnApiDelegate = NnApiDelegate()
            options.addDelegate(nnApiDelegate)
        }

        try {
            interpreter = Interpreter(loadModelFile(context.assets, modelName), options)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        imageSize = 128
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelFilename: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classifyImage(bitmap: Bitmap): String {
        val byteBuffer = preprocessImage(bitmap)
        return runInference(byteBuffer)
    }

    private fun runInference(byteBuffer: ByteBuffer): String {
        val output = Array(1) { FloatArray(9) }
        interpreter.run(byteBuffer, output)
        return processOutput(output)
    }

    private fun processOutput(outputTensorBuffer: Array<FloatArray>): String {
        var maxIndex = 0
        var maxValue = outputTensorBuffer[0][0]
        for (i in outputTensorBuffer[0].indices) {
            if (outputTensorBuffer[0][i] > maxValue) {
                maxValue = outputTensorBuffer[0][i]
                maxIndex = i
            }
        }

        val ClassName: List<String> = listOf("Алтайская красавица", "Заветное", "Мелба", "Жебровское", "Перун",
            "Мучнистая роса", "Парша", "Плодовая гниль", "Черный рак")

        return ClassName[maxIndex]
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true)
        if (resizedBitmap == null) {
            Log.e("PreprocessImage", "Failed to resize image")
            throw IllegalArgumentException("Image resizing failed")
        }

        val pixels = IntArray(imageSize * imageSize)
        resizedBitmap.getPixels(pixels, 0, imageSize, 0, 0, imageSize, imageSize)

        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        Log.d("PreprocessImage", "Image resized to $imageSize x $imageSize")
        Log.d("PreprocessImage", "Pixels array length: ${pixels.size}")

        if (pixels.size != imageSize * imageSize) {
            Log.e("PreprocessImage", "Pixels array size does not match expected size")
            throw IllegalStateException("Pixels array size mismatch")
        }

        for ((index, pixel) in pixels.withIndex()) {
            try {
                val r = (pixel shr 16 and 0xFF).toFloat()
                val g = (pixel shr 8 and 0xFF).toFloat()
                val b = (pixel and 0xFF).toFloat()

                byteBuffer.putFloat(r / 255.0f)
                byteBuffer.putFloat(g / 255.0f)
                byteBuffer.putFloat(b / 255.0f)
            } catch (e: Exception) {
                Log.e("PreprocessImage", "Error processing pixel at index $index: ${e.message}")
                throw e
            }
        }
        return byteBuffer
    }
}