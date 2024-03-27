package com.example.basiccamera.ui.component

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
import timber.log.Timber

@Composable
fun CameraComponent(
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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
    val preview: androidx.camera.core.Preview = androidx.camera.core.Preview.Builder().build()

    val cameraViewModel: CameraComponentModel = hiltViewModel()
    val imageBitmap by cameraViewModel.imageBitmap.collectAsState()
    var processing: Boolean by remember { mutableStateOf(false) }

    // 카메라 바인딩
    // cameraProvicer를 통해 ProcessCameraProvider를 비동기적으로 가져와 카메라의 생명주기를 현재 카메라 컴포넌트 생명주기와 바인딩
    // preview, imageCapture를 lifecycleOwner에 바인딩하여 카메라 미리보기와 이미지 캡쳐를 구현
    // 카메라 세션 준비가 완료되면 프리뷰를 화면에 표시
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

    // takeAction 상태가 true로 변경될 때, imageCapture의 takePicture 메서드를 호출하여 이미지 캡쳐
    // 촬영 성공 시, onCaptureSuccess 콜백에서 이미지를 비트맵으로 변환하고, 사용자 지정 작업 수행
    // 실패 시, onError 콜백에서 실패 이유를 로그로 출력하고, 사용자 지정 작업 수행
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