package pl.intexsoft.photoapp.core.domain

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.intexsoft.photoapp.core.model.Photo

class GetPhotosFromGalleryUseCase(
    private val context: Context,
) {
    suspend operator fun invoke(): Result<List<Photo>> =
        withContext(Dispatchers.IO) {
            val resolver: ContentResolver = context.applicationContext.contentResolver

            val imageCollection: Uri =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val selection =
                MediaStore.Images.ImageColumns.RELATIVE_PATH + " like ? "
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
            )
            val selectionArgs = arrayOf("%PhotoApp%")

            val sortOrder = MediaStore.MediaColumns.DATE_ADDED + " COLLATE NOCASE DESC"

            val itemList: MutableList<Photo> = mutableListOf()

            val result: Result<List<Photo>> = kotlin.runCatching {
                resolver.query(
                    imageCollection,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val widthPathColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)
                    val heightPathColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)

                    val scales = arrayOf(1f, 9f / 16f, 3f / 4f)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val width = cursor.getInt(widthPathColumn)
                        val height = cursor.getInt(heightPathColumn).toFloat()

                        val contentUri = ContentUris.withAppendedId(imageCollection, id)

                        val correctedScale = scales.closestValue(width / height) ?: 1f

                        itemList.add(
                            Photo(
                                name = displayName,
                                contentUri = contentUri,
                                scale = correctedScale,
                                tag = null
                            )
                        )
                    }
                    cursor.close()
                    Result.success(itemList)
                }
            }.getOrElse { exception: Throwable ->
                exception.message?.let(::println)
                Result.failure(exception)
            } ?: run {
                Result.failure(Exception("Couldn't create file for gallery"))
            }

            result
        }

    private fun Array<Float>.closestValue(value: Float): Float? {
        return minByOrNull { kotlin.math.abs(value - it) }
    }
}