package pl.intexsoft.photoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import pl.intexsoft.photoapp.feature.camera.CameraScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            pl.intexsoft.photoapp.core.ui.theme.PhotoAppTheme {
                Navigator(
                    screen = CameraScreen
                ) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}