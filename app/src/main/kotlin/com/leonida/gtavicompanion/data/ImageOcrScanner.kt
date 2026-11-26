package com.leonida.gtavicompanion.data

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Offline on-device OCR powered by Google ML Kit Text Recognition.
 * Reads text directly from TV camera captures or PlayStation App gallery screenshots.
 */
class ImageOcrScanner(private val context: Context) {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    suspend fun recognizeTextFromUri(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            val text = suspendCancellableCoroutine<String> { cont ->
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        if (cont.isActive) cont.resume(visionText.text)
                    }
                    .addOnFailureListener { exc ->
                        if (cont.isActive) cont.resumeWithException(exc)
                    }
            }
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
