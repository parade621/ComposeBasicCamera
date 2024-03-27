package com.example.basiccamera.ui.screen.camera

import android.graphics.Bitmap

data class CameraState(
    val showLoading: Boolean = false,
    val permitCamera: Boolean = false,
    val selfCam: Boolean = false,
    val capture: Boolean = false,
    val imageBitmap: Bitmap? = null,
    val logString: String = ""
)
