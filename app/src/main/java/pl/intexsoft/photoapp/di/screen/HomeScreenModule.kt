package pl.intexsoft.photoapp.di.screen

import cafe.adriel.voyager.core.registry.screenModule
import pl.intexsoft.photoapp.core.navigation.SharedScreen
import pl.intexsoft.photoapp.feature.camera.CameraScreen
import pl.intexsoft.photoapp.feature.gallery.GalleryScreen

val homeScreenModule = screenModule {
    register<SharedScreen.Camera> {
        CameraScreen
    }
    register<SharedScreen.Gallery> {
        GalleryScreen
    }
}
