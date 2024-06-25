package com.example.basiccamera.presentation.ui.component

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.example.basiccamera.domain.camera.CameraComponentModel
import timber.log.Timber

@Composable
fun CameraComponent(
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    modifier: Modifier = Modifier,
    selfMode: Boolean,
    takeAction: Boolean,
    receiveImageUrl: (Bitmap?) -> Unit
) {
    val cameraSelector = if (selfMode) {
        CameraSelector.DEFAULT_FRONT_CAMERA
    } else {
        CameraSelector.DEFAULT_BACK_CAMERA
    }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
    val preview: androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()

    val cameraViewModel: CameraComponentModel = hiltViewModel()
    val imageBitmap by cameraViewModel.imageBitmap.collectAsState()
    var processing: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(cameraProviderFuture, cameraSelector) {
        cameraProviderFuture.addListener({
            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Timber.e("Camera binding failed")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    LaunchedEffect(takeAction, imageBitmap) {
        if (takeAction && !processing) {
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        processing = true
                        cameraViewModel.processImageCapture(image, selfMode)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Timber.e("Image capture failed ${exception.message}")
                        receiveImageUrl(null)
                    }
                }
            )
        }
        if (imageBitmap != null) {
            receiveImageUrl(imageBitmap)
            cameraViewModel.clearImageBitmap()
            processing = false
        }
    }

    AndroidView(
        modifier = modifier.clipToBounds(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        update = { previewView ->
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    )
}