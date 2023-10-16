package pl.intexsoft.photoapp.core.domain.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import pl.intexsoft.photoapp.core.domain.DeletePhotosFromGalleryUseCase
import pl.intexsoft.photoapp.core.domain.GetPhotosFromGalleryUseCase
import pl.intexsoft.photoapp.core.domain.SavePhotoToGalleryUseCase

val domainModule = module {
    factoryOf(::SavePhotoToGalleryUseCase)
    factoryOf(::GetPhotosFromGalleryUseCase)
    factoryOf(::DeletePhotosFromGalleryUseCase)
}
