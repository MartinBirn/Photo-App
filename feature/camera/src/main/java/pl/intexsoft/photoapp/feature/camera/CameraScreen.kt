package pl.intexsoft.photoapp.feature.camera

import android.util.Log
import android.view.OrientationEventListener
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.intexsoft.photoapp.core.navigation.SharedScreen
import java.util.concurrent.Executor

object CameraScreen : Screen {

    private fun readResolve(): Any = CameraScreen

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {
        Log.d("CameraScreen", "Content()")
        val screenModel = getScreenModel<CameraScreenModel>()
        val screenState by screenModel.state.collectAsState()

        val navigator = LocalNavigator.current
        val galleryScreen = rememberScreen(SharedScreen.Gallery)

        val cameraPermissionState: PermissionState =
            rememberPermissionState(android.Manifest.permission.CAMERA)

        LaunchedEffect(key1 = Unit) {
            if (!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
                cameraPermissionState.launchPermissionRequest()
            }
        }

        if (cameraPermissionState.status.isGranted) {
            CameraContent(
                lensFacing = CameraSelector.LENS_FACING_BACK,
                onGalleryClicked = screenModel::onGalleryClicked,
                onImageCaptured = screenModel::onImageCaptured,
                onError = {}
            )
        } else {

        }

        LaunchedEffect(Unit) {
            launch {
                screenModel.openGallery.receiveAsFlow().collectLatest {
                    navigator?.push(galleryScreen)
                }
            }
        }
    }

    @Composable
    private fun CameraContent(
        lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
        cameraSelector: CameraSelector =
            if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.DEFAULT_BACK_CAMERA
            else CameraSelector.DEFAULT_FRONT_CAMERA,
        onGalleryClicked: () -> Unit,
        onImageCaptured: (image: ImageProxy) -> Unit,
        onError: (exception: Exception) -> Unit
    ) {
        val context = LocalContext.current
        val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        val imageCapture = remember { ImageCapture.Builder().build() }

        var iconRotation by remember { mutableFloatStateOf(0f) }
        val orientationListener by remember {
            mutableStateOf(
                object : OrientationEventListener(context) {
                    override fun onOrientationChanged(orientation: Int) {
                        Log.d("CameraScreen", "orientation: $orientation")
                        iconRotation = when (orientation) {
                            in 0..30 -> 0f
                            in 150..210 -> 180f
                            in 60..120 -> 270f
                            in 240..300 -> 90f
                            else -> iconRotation
                        }
                        if (orientation == ORIENTATION_UNKNOWN) return
                        imageCapture.targetRotation = UseCase.snapToSurfaceRotation(orientation)
                    }
                }
            )
        }
        DisposableEffect(lifecycleOwner) {
            orientationListener.enable()
            onDispose {
                orientationListener.disable()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }.also { previewView ->
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().apply {
                                setSurfaceProvider(previewView.surfaceProvider)
                            }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (ex: Exception) {
                                onError(ex)
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                }
            )
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = pl.intexsoft.photoapp.core.ui.R.drawable.ic_gallery
                ),
                contentDescription = stringResource(
                    id = pl.intexsoft.photoapp.core.ui.R.string.open_gallery
                ),
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.TopEnd)
                    .rotate(iconRotation)
                    .clickable(onClick = onGalleryClicked),
                tint = Color.Unspecified
            )
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = pl.intexsoft.photoapp.core.ui.R.drawable.ic_camera
                ),
                contentDescription = stringResource(
                    id = pl.intexsoft.photoapp.core.ui.R.string.take_picture
                ),
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomCenter)
                    .clip(shape = CircleShape)
                    .rotate(iconRotation)
                    .clickable {
                        val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
                        imageCapture.takePicture(
                            mainExecutor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    onImageCaptured(image)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    onError(exception)
                                }
                            }
                        )
                    },
                tint = Color.Unspecified
            )
        }
    }
}