# Photo-App

Technologies:

1. Kotlin
2. MVI architecture
3. Compose
4. Voyager
5. Koin
6. Coil
7. CameraX

Functionality:

1. Camera screen for taking photos and device rotation support
2. Gallery screen with sorting (from latest to oldest)
3. Multiple selection
4. Deleting of selected photos

# Camera (Screen):

* The screen was made using Compose
* The camera functionality was implemented using the CameraX library
* Support for photography in portrait and landscape mode (with icons rotated and their position
  saved) has been implemented

# Gallery (Screen):

* The screen was made using Compose
* Images are saved as files to the device memory
* Images are loaded asynchronously and displayed via the Coil library
* Support for selecting and deleting relevant images has been implemented
* (NOT IMPLEMENTED) Tag creation and filtering by tags was not implemented due to time constraints.
  If it had been used, the SQLDelight library would have been applied due to convenient work with
  Kotlin

# Other

* Navigation between screens is implemented through the Voyager library due to its simplicity
* Dependency injection is performed using the Koin library because of its simplicity and
  flexibility, as well as support for Compose and Voyager
