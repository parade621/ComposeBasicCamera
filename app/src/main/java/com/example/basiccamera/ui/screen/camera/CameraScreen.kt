package com.example.basiccamera.ui.screen.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basiccamera.MainActivity
import com.example.basiccamera.R
import com.example.basiccamera.ui.component.CameraComponent
import com.example.basiccamera.ui.component.CheckPermissionComponent
import com.example.basiccamera.ui.theme.BasicCameraTheme

@Composable
fun CameraScreen(
    state: CameraState,
    onEvent: (CameraEvent) -> Unit
) {
    // Preview 표시를 위한 context value
    val context = if (!LocalInspectionMode.current) {
        LocalContext.current as MainActivity
    } else {
        null
    }

    CheckPermissionComponent { permit ->
        onEvent(CameraEvent.GrantCameraPermission(permit))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.DarkGray),
            Alignment.Center
        ) {
            Text(
                text = "Camera Preview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .clickable {
                if (!state.capture) {
                    onEvent(CameraEvent.Capture)
                }
            }
        ) {
            if (context != null && state.permitCamera) {
                CameraComponent(
                    modifier = Modifier
                        .fillMaxSize(),
                    selfMode = state.selfCam,
                    takeAction = state.capture,
                    receiveImageUrl = { bitmap ->
                        if (bitmap != null)
                            onEvent(CameraEvent.GetImageBitmap(bitmap))
                        else {
                            // Error 처리
                        }
                        onEvent(CameraEvent.CaptureProcessed)
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(Color.Transparent),
                Alignment.Center
            ) {
                if (state.imageBitmap != null) {
                    Image(
                        bitmap = state.imageBitmap.asImageBitmap(),
                        contentDescription = "latest photo",
                        modifier = Modifier.clickable {
                            // 사진 표시ㄱㄱ
                        }
                    )
                }
            }
            Image(
                painterResource(id = R.drawable.baseline_photo_camera_36),
                contentDescription = "capture btn",
                modifier = Modifier.clickable {
                    if (!state.capture) {
                        onEvent(CameraEvent.Capture)
                    }
                }
            )
            Image(
                painterResource(id = R.drawable.baseline_cameraswitch_36),
                contentDescription = "capture btn",
                modifier = Modifier.clickable {
                    if (!state.capture) {
                        onEvent(CameraEvent.SelfCamMode)
                    }
                }
            )
        }
    }

    if (state.showLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.testTag("ProgressLoading"))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    BasicCameraTheme {
        CameraScreen(CameraState()) {}
    }
}