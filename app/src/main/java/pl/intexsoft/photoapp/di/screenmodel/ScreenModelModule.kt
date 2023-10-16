package pl.intexsoft.photoapp.di.screenmodel

import org.koin.dsl.module
import pl.intexsoft.photoapp.feature.camera.di.cameraModule
import pl.intexsoft.photoapp.feature.gallery.di.galleryModule

val screenModelModule = module {
    includes(
        cameraModule,
        galleryModule
    )
}