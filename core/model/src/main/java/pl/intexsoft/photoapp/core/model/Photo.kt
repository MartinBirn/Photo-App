package pl.intexsoft.photoapp.core.model

import android.net.Uri

data class Photo(
    val name: String,
    val contentUri: Uri,
    val scale: Float,
    val tag: String?
)
