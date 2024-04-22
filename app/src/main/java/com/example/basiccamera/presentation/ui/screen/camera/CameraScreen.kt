package com.example.basiccamera.presentation.ui.screen.camera

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basiccamera.MainActivity
import com.example.basiccamera.R
import com.example.basiccamera.presentation.ui.component.CameraComponent
import com.example.basiccamera.presentation.ui.component.CheckPermissionComponent
import com.example.basiccamera.presentation.ui.theme.BasicCameraTheme

@OptIn(ExperimentalFoundationApi::class)
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
    val pagerState = rememberPagerState(pageCount = {
        CameraMode.entries.size
    })

    CheckPermissionComponent { permit ->
        onEvent(CameraEvent.GrantCameraPermission(permit))
    }


    // Camera Component
    Box(Modifier.fillMaxSize()) {
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

        // Top UI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(color = Color.White.copy(alpha = 0.3f)),
            Alignment.Center
        ) {
            Text(
                text = "Camera Preview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Bottom UI
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .background(color = Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().padding(top=10.dp),
                contentPadding = PaddingValues(horizontal = 130.dp),
            ) { page ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 12.dp)
                        .background(
                            color = if (pagerState.currentPage == page) Color.Gray else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = CameraMode.entries[page].value,
                        fontSize = 18.sp,
                        fontWeight = if (pagerState.currentPage == page) FontWeight.Bold else FontWeight.Normal,
                        color = if (pagerState.currentPage == page) Color.White else Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }

            // 여기까지
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .border(border = BorderStroke(2.dp, Color.White), shape = CircleShape)
                        .size(60.dp)
                        .background(Color.Transparent),
                    Alignment.Center
                ) {
                    if (state.imageBitmap != null) {
                        Image(
                            bitmap = state.imageBitmap.asImageBitmap(),
                            contentDescription = "latest photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .clickable {
                                    // 사진 표시ㄱㄱ
                                }
                        )
                    }
                }

                Image(
                    painterResource(id = R.drawable.capture_btn_white),
                    contentDescription = "capture btn",
                    modifier = Modifier
                        .size(85.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (!state.capture) {
                                onEvent(CameraEvent.Capture)
                            }
                        }
                )

                Box(
                    modifier = Modifier
                        .border(border = BorderStroke(2.dp, Color.White), shape = CircleShape)
                        .size(60.dp)
                        .background(Color.Transparent),
                    Alignment.Center
                ) {
                    Image(
                        painterResource(id = R.drawable.flip_camera_white),
                        contentDescription = "capture btn",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (!state.capture) {
                                    onEvent(CameraEvent.SelfCamMode)
                                }
                            }
                    )
                }
            }
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