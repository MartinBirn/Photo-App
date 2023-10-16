package pl.intexsoft.photoapp.feature.camera

import androidx.camera.core.ImageProxy
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pl.intexsoft.photoapp.core.common.extension.rotateBitmap
import pl.intexsoft.photoapp.core.domain.SavePhotoToGalleryUseCase

class CameraScreenModel(
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
) : StateScreenModel<CameraScreenModel.State>(State.Init) {

    val openGallery = Channel<Unit>()

    fun onGalleryClicked() {
        CoroutineScope(coroutineScope.coroutineContext + Dispatchers.IO).launch {
            openGallery.send(Unit)
        }
    }

    fun onImageCaptured(image: ImageProxy) {
        CoroutineScope(coroutineScope.coroutineContext + Dispatchers.IO).launch {
            val bitmap = image
                .toBitmap()
                .rotateBitmap(image.imageInfo.rotationDegrees)
            savePhotoToGalleryUseCase(bitmap)
        }
    }

    sealed class State {
        data object Init : State()
    }
}