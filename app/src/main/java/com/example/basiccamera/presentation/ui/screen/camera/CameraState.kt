package com.example.basiccamera.presentation.ui.screen.camera

import android.graphics.Bitmap

data class CameraState(
    val showLoading: Boolean = false,
    val mode: CameraMode = CameraMode.CAMERA,
    val permitCamera: Boolean = false,
    val selfCam: Boolean = false,
    val capture: Boolean = false,
    val imageBitmap: Bitmap? = null,
    val logString: String = ""
)

enum class CameraMode(val value: String) {
    CAMERA("사진"),
    ViDEO("동영상")
}
