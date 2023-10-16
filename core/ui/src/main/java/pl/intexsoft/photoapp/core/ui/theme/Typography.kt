@file:Suppress("unused", "UnusedReceiverParameter")

package pl.intexsoft.photoapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val typography = Typography(
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp
    )
)

val Typography.headerMedium: TextStyle
    @Composable
    get() = TextStyle(
        fontSize = 30.sp,
        fontFamily = RalewayFontFamily,
        fontWeight = FontWeight.Medium,
        color = PhotoAppColor.Black,
    )

val Typography.body2Medium: TextStyle
    @Composable
    get() = TextStyle(
        fontSize = 16.sp,
        fontFamily = RalewayFontFamily,
        fontWeight = FontWeight.Medium,
        color = PhotoAppColor.Black,
    )

val Typography.bodyBold: TextStyle
    @Composable
    get() = TextStyle(
        fontSize = 16.sp,
        fontFamily = RalewayFontFamily,
        fontWeight = FontWeight.Bold,
        color = PhotoAppColor.White,
    )
