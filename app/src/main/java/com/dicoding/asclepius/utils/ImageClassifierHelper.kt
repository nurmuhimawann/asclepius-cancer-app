package com.dicoding.asclepius.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import com.dicoding.asclepius.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "cancer_classification.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {

    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: Exception) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, "Internal error at setup: ${e.message}")
        }
    }

    fun classifyImage(imageUri: Uri) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        try {
            val bitmap = uriToBitmap(imageUri) ?: throw Exception("Failed to load bitmap")

            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(CastOp(DataType.FLOAT32))
                .build()

            val tensorImage = TensorImage(DataType.UINT8)
            tensorImage.load(bitmap)
            val processedImage = imageProcessor.process(tensorImage)

            val inferenceTime = SystemClock.uptimeMillis()
            val results = imageClassifier?.classify(processedImage)
            val elapsedTime = SystemClock.uptimeMillis() - inferenceTime

            classifierListener?.onResult(results, elapsedTime)

        } catch (e: Exception) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG, "Error during image classification: ${e.message}")
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e(TAG, "uriToBitmap: ${e.message}")
            null
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResult(
            result: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}
