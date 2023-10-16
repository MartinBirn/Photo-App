package pl.intexsoft.photoapp.core.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen : ScreenProvider {
    data object Camera : SharedScreen()
    data object Gallery : SharedScreen()
}
