package pl.intexsoft.photoapp.feature.gallery

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import pl.intexsoft.photoapp.core.domain.DeletePhotosFromGalleryUseCase
import pl.intexsoft.photoapp.core.domain.GetPhotosFromGalleryUseCase
import pl.intexsoft.photoapp.core.ui.model.PhotoUi
import pl.intexsoft.photoapp.core.ui.model.TagUi
import pl.intexsoft.photoapp.core.ui.model.toDomainModel
import pl.intexsoft.photoapp.core.ui.model.toUiModel

class GalleryScreenModel(
    private val getPhotosFromGalleryUseCase: GetPhotosFromGalleryUseCase,
    private val deletePhotosFromGalleryUseCase: DeletePhotosFromGalleryUseCase
) : StateScreenModel<GalleryScreenModel.State>(State.Loading) {

    val openCamera = Channel<Unit>()
    val openPreviousScreen = Channel<Unit>()

    private var photos: List<PhotoUi> = emptyList()
    private var tags: List<String> = emptyList()

    init {
        mutableState.value = State.Loading

        CoroutineScope(coroutineScope.coroutineContext + Dispatchers.IO).launch {
            fetchPhotos()
            mutableState.value = State.Default(photos = photos)
        }
    }

    fun onBackPressed() {
        if (state.value is State.PhotoSelection) {
            onCancelClicked()
        } else {
            openPreviousScreen.trySend(Unit)
        }
    }

    fun onCameraClicked() {
        CoroutineScope(coroutineScope.coroutineContext + Dispatchers.IO).launch {
            openCamera.send(Unit)
        }
    }

    fun onCancelClicked() {
        mutableState.value = State.Default(
            photos = photos.map { if (it.isChecked) it.copy(isChecked = false) else it }
        )
    }

    fun onDeleteClicked() {
        CoroutineScope(coroutineScope.coroutineContext + Dispatchers.IO).launch {
            mutableState.value = State.Loading

            val result = deletePhotosFromGalleryUseCase(
                photos = photos.mapNotNull { if (it.isChecked) it.toDomainModel() else null }
            )
            if (result.isFailure) {

            }

            fetchPhotos()
            mutableState.value = State.Default(photos = photos)
        }
    }

    fun onFilterClicked() {
        mutableState.value = State.FilterSelection(
            photos = photos,
            tags = listOf( // just hardcoded example
                TagUi("Landschaft ", false),
                TagUi("Portrait", false),
                TagUi("Licht", false),
                TagUi("Abstrakt", false),
                TagUi("Architektur", false)
            )
        )
    }

    fun onFilterSelected() {

    }

    fun onCloseFiltersClicked() {
        mutableState.value = State.Default(
            photos = photos.map { if (it.isChecked) it.copy(isChecked = false) else it }
        )
    }

    fun onPhotoClicked(name: String) {
        photos = photos.map { photo ->
            if (photo.name == name) {
                photo.copy(isChecked = !photo.isChecked)
            } else {
                photo
            }
        }
        val selections = photos.filter { it.isChecked }.size
        mutableState.value = State.PhotoSelection(photos = photos, selections = selections)
    }

    fun onCheckAllClicked() {
        val allChecked = photos.filter { it.isChecked }.size == photos.size
        photos = if (allChecked) {
            photos.map { if (!it.isChecked) it else it.copy(isChecked = false) }
        } else {
            photos.map { if (it.isChecked) it else it.copy(isChecked = true) }
        }
        val selections = photos.filter { it.isChecked }.size
        mutableState.value = State.PhotoSelection(photos = photos, selections = selections)
    }

    private suspend fun fetchPhotos() {
        photos = getPhotosFromGalleryUseCase().getOrNull().orEmpty().map { it.toUiModel() }
    }

    sealed class State {
        data object Loading : State()
        data class Default(val photos: List<PhotoUi>) : State()
        data class FilterSelection(val photos: List<PhotoUi>, val tags: List<TagUi>) : State()
        data class PhotoSelection(val photos: List<PhotoUi>, val selections: Int) : State()
    }
}