package com.example.basiccamera.presentation.ui.screen.camera

import android.graphics.Bitmap

sealed class CameraEvent {
    object SelfCamMode : CameraEvent()
    object Capture : CameraEvent()
    object CaptureProcessed : CameraEvent()

    data class ChangeMode(val mode: CameraMode) : CameraEvent()
    data class GetImageBitmap(val bitmap: Bitmap) : CameraEvent()
    data class GrantCameraPermission(val permission: Boolean) : CameraEvent()
}