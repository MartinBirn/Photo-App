package pl.intexsoft.photoapp.core.domain

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

class SavePhotoToGalleryUseCase(
    private val context: Context,
) {

    suspend operator fun invoke(photoBitmap: Bitmap): Result<Unit> =
        withContext(Dispatchers.IO) {
            val resolver: ContentResolver = context.applicationContext.contentResolver

            val imageCollection: Uri =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            // Publish a new image.
            val nowTimestamp: Long = System.currentTimeMillis()
            val imageContentValues: ContentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$nowTimestamp.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.DATE_TAKEN, nowTimestamp)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/PhotoApp")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
                put(MediaStore.Images.Media.DATE_TAKEN, nowTimestamp)
                put(MediaStore.Images.Media.DATE_ADDED, nowTimestamp)
                put(MediaStore.Images.Media.DATE_MODIFIED, nowTimestamp)
                //put(MediaStore.Images.Media.AUTHOR, "PhotoApp")
                //put(MediaStore.Images.Media.DESCRIPTION, "")
            }

            val imageMediaStoreUri: Uri? = resolver.insert(imageCollection, imageContentValues)

            // Write the image data to the new Uri.
            val result: Result<Unit> = imageMediaStoreUri?.let { uri ->
                kotlin.runCatching {
                    resolver.openOutputStream(uri).use { outputStream: OutputStream? ->
                        checkNotNull(outputStream) { "Couldn't create file for gallery, MediaStore output stream is null" }
                        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }

                    imageContentValues.clear()
                    imageContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, imageContentValues, null, null)

                    Result.success(Unit)
                }.getOrElse { exception: Throwable ->
                    exception.message?.let(::println)
                    resolver.delete(uri, null, null)
                    Result.failure(exception)
                }
            } ?: run {
                Result.failure(Exception("Couldn't create file for gallery"))
            }

            return@withContext result
        }
}
