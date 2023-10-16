package pl.intexsoft.photoapp.core.common.extension

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotateBitmap(rotationDegrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(rotationDegrees.toFloat())
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
