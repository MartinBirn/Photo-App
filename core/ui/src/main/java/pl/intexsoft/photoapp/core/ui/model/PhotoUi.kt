package pl.intexsoft.photoapp.core.ui.model

import android.net.Uri
import pl.intexsoft.photoapp.core.model.Photo

data class PhotoUi(
    val name: String,
    val contentUri: Uri,
    val scale: Float,
    val tag: String?,
    val isChecked: Boolean
)

fun Photo.toUiModel(): PhotoUi {
    return PhotoUi(
        name = name,
        contentUri = contentUri,
        scale = scale,
        tag = tag,
        isChecked = false,
    )
}

fun PhotoUi.toDomainModel(): Photo {
    return Photo(
        name = name,
        contentUri = contentUri,
        scale = scale,
        tag = tag
    )
}
