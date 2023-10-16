package pl.intexsoft.photoapp

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import pl.intexsoft.photoapp.di.appModule
import pl.intexsoft.photoapp.di.screen.homeScreenModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }

        ScreenRegistry {
            homeScreenModule()
        }
    }
}