package pl.intexsoft.photoapp.feature.gallery.di

import org.koin.dsl.module
import pl.intexsoft.photoapp.core.domain.di.domainModule
import pl.intexsoft.photoapp.feature.gallery.GalleryScreenModel

val galleryModule = module {
    includes(domainModule)

    factory {
        GalleryScreenModel(
            getPhotosFromGalleryUseCase = get(),
            deletePhotosFromGalleryUseCase = get()
        )
    }
}
