package com.example.basiccamera.ui.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CameraComponentModel : ViewModel() {
    private val _imageBitmap = MutableStateFlow<Bitmap?>(null)
    val imageBitmap: StateFlow<Bitmap?> = _imageBitmap

    fun processImageCapture(image: ImageProxy, selfMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = getProcessedBitmap(image, selfMode)
            _imageBitmap.value = bitmap
            image.close()
        }
    }

    fun clearImageBitmap() {
        _imageBitmap.value = null
    }

    private fun getProcessedBitmap(image: ImageProxy, selfMode: Boolean): Bitmap {
        val originalBitmap = imageProxyToBitmap(image, selfMode)
        return resizeBitmapImage(originalBitmap) ?: originalBitmap
    }

    private fun imageProxyToBitmap(image: ImageProxy, selfMode: Boolean): Bitmap {
        val buffer = image.planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val rotation = image.imageInfo.rotationDegrees
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        var rotationBitmap = if (rotation != 0) {
            val matrix = Matrix()
            matrix.setRotate(
                rotation.toFloat(),
                bitmap.width.toFloat() / 2,
                bitmap.height.toFloat() / 2
            )

            Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
        } else {
            bitmap
        }

        if (selfMode) {
            val mirrorMatrix = Matrix().apply {
                preScale(
                    -1.0f,
                    1.0f,
                    rotationBitmap.width.toFloat() / 2,
                    rotationBitmap.height.toFloat() / 2
                )
            }
            rotationBitmap = Bitmap.createBitmap(
                rotationBitmap,
                0,
                0,
                rotationBitmap.width,
                rotationBitmap.height,
                mirrorMatrix,
                true
            )
        }

        return resizeBitmapImage(rotationBitmap, 1500) ?: rotationBitmap
    }

    private fun resizeBitmapImage(source: Bitmap, maxResolutionInfo: Int = 1500): Bitmap? {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        val rate: Float
        if (width > height) {
            if (maxResolutionInfo < width) {
                rate = maxResolutionInfo / width.toFloat()
                newHeight = (height * rate).toInt()
                newWidth = maxResolutionInfo
            }
        } else {
            if (maxResolutionInfo < height) {
                rate = maxResolutionInfo / height.toFloat()
                newWidth = (width * rate).toInt()
                newHeight = maxResolutionInfo
            }
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }
}