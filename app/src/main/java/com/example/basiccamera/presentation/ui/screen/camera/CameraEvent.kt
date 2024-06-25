package com.example.basiccamera.presentation.ui.screen.camera

import android.graphics.Bitmap

sealed class CameraEvent {
    data object SelfCamMode : CameraEvent()
    data object Capture : CameraEvent()
    data object CaptureProcessed : CameraEvent()

    data class ChangeMode(val mode: CameraMode) : CameraEvent()
    data class GetImageBitmap(val bitmap: Bitmap) : CameraEvent()
    data class GrantCameraPermission(val permission: Boolean) : CameraEvent()
}