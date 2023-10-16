package pl.intexsoft.photoapp.feature.camera.di

import org.koin.dsl.module
import pl.intexsoft.photoapp.core.domain.di.domainModule
import pl.intexsoft.photoapp.feature.camera.CameraScreenModel

val cameraModule = module {
    includes(domainModule)

    factory {
        CameraScreenModel(
            savePhotoToGalleryUseCase = get()
        )
    }
}
