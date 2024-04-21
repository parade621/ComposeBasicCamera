package com.example.basiccamera.presentation.ui.component

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun CheckPermissionComponent(onPermissionGranted: (Boolean) -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 부여된 경우
                onPermissionGranted(true)
            } else {
                Toast.makeText(context, "카메라 권한이 필요합니다. 세팅에서 직접 설정하셔야합니다.", Toast.LENGTH_SHORT)
                    .show()
                // 권한 거부 시 로직은 따로 처리 안했습니다.
            }
        }
    )

    LaunchedEffect(key1 = true) {
        val cameraPermission = Manifest.permission.CAMERA
        val permissionStatus = ContextCompat.checkSelfPermission(context, cameraPermission)

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            // 권한이 부여되지 않은 경우, 권한 요청
            permissionLauncher.launch(cameraPermission)
        } else {
            // 이미 권한이 부여된 경우
            onPermissionGranted(true)
        }
    }
}
