package com.example.basiccamera.presentation.ui.screen.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.basiccamera.presentation.ui.screen.camera.CameraEvent
import com.example.basiccamera.presentation.ui.screen.camera.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class CameraViewModel : ViewModel() {
    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    fun onEvent(event: CameraEvent) {
        when (event) {
            CameraEvent.SelfCamMode -> selfCamMode()
            CameraEvent.Capture -> captureImage()
            CameraEvent.CaptureProcessed -> captureProcessed()
            is CameraEvent.GetImageBitmap -> getImageBitmap(event.bitmap)
            is CameraEvent.GrantCameraPermission -> grantCameraPermission(event.permission)
            is CameraEvent.ChangeMode -> changeMode(event.mode)
        }
    }

    private fun selfCamMode() {
        _state.update { it.copy(selfCam = !_state.value.selfCam) }
        Timber.e("selfCamMode: ${_state.value.selfCam}")
    }

    private fun captureImage() {
        _state.update { it.copy(capture = true, showLoading = true) }
    }

    private fun captureProcessed() {
        _state.update { it.copy(capture = false, showLoading = false) }
    }

    private fun getImageBitmap(bitmap: Bitmap) {
        _state.update { it.copy(imageBitmap = bitmap) }
    }

    private fun grantCameraPermission(permission: Boolean) {
        _state.update { it.copy(permitCamera = permission) }
    }

    private fun changeMode(mode: CameraMode) {
        _state.update { it.copy(mode = mode) }
    }

}