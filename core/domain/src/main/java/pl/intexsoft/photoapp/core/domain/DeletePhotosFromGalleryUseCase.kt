package pl.intexsoft.photoapp.core.domain

import android.content.ContentResolver
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.intexsoft.photoapp.core.model.Photo

class DeletePhotosFromGalleryUseCase(
    private val context: Context,
) {
    suspend operator fun invoke(photos: List<Photo>): Result<Unit> = withContext(Dispatchers.IO) {
        val resolver: ContentResolver = context.applicationContext.contentResolver

        return@withContext kotlin.runCatching {
            photos.forEach { photo ->
                resolver.delete(photo.contentUri, null, null)
            }
            return@runCatching Result.success(Unit)
        }.getOrElse { exception: Throwable ->
            exception.message?.let(::println)
            Result.failure(exception)
        }
    }
}