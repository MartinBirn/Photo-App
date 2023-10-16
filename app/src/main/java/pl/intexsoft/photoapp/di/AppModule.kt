package pl.intexsoft.photoapp.di

import org.koin.dsl.module
import pl.intexsoft.photoapp.di.screenmodel.screenModelModule

val appModule = module {
    includes(
        screenModelModule
    )
}