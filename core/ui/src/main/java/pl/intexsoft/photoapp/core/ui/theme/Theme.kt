package pl.intexsoft.photoapp.core.ui.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Light default theme color scheme
 */
@VisibleForTesting
val LightDefaultColorScheme = lightColorScheme(
    primary = PhotoAppColor.White,
    onPrimary = PhotoAppColor.RedDark,
    secondary = PhotoAppColor.RedLight,
    onSecondary = PhotoAppColor.White,
    background = PhotoAppColor.White,
    onBackground = PhotoAppColor.Black,
    onSurfaceVariant = PhotoAppColor.BlueLight
)

/**
 * Dark default theme color scheme
 */
@VisibleForTesting
val DarkDefaultColorScheme = darkColorScheme(
    primary = PhotoAppColor.White,
    onPrimary = PhotoAppColor.RedDark,
    secondary = PhotoAppColor.RedLight,
    onSecondary = PhotoAppColor.White,
    background = PhotoAppColor.White,
    onBackground = PhotoAppColor.Black,
    onSurfaceVariant = PhotoAppColor.BlueLight
)

/**
 * PhotoApp theme.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 */
@Composable
fun PhotoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = PhotoAppTheme(
    darkTheme = darkTheme,
    disableDynamicTheming = true,
    content = content
)

/**
 * PhotoApp theme. This is an internal only version, to allow disabling dynamic theming
 * in tests.
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 * @param disableDynamicTheming If `true`, disables the use of dynamic theming, even when it is
 *        supported.
 */
@Composable
internal fun PhotoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    disableDynamicTheming: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (!disableDynamicTheming && supportsDynamicTheming()) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) {
            DarkDefaultColorScheme
        } else {
            LightDefaultColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
private fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
