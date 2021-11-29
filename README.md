# AllTrails at Lunch (Rebecca Blum's version)

## Architecture

This is a single-activity app built with Jetpack Compose UI and MVVM architecture. There is a
single, shared viewModel to support dynamic updating of the restaurant search regardless of which
part of the UI is being displayed. A repository wraps network calls and provides data updates to the
viewModel when requested.

## Key Libraries

- Jetpack Compose for UI
- Kotlin coroutines for reactive/async
- Google Maps for map view
- GMS location services for device location
- Jetpack Compose Accompanist Permissions for location permission prompt
- Retrofit for network calls
- Dagger Hilt for DI
- Coil for image loading
- Mockk for unit testing

## Limitations

- Jetpack Compose is still relatively new to me, and there's a lot of gray area for how to break up
  view classes. I tried to focus on a cleaner viewModel and repository and less on perfect
  separation of concerns in Compose.
- Google Maps' MapView is not off-the-shelf compatible with Compose, so I made heavy reference to
  this project: https://johnoreilly.dev/posts/jetpack-compose-google-maps/
- I could not find a way to use Maps' `InfoWindowAdapter` with Composable views, so I instead popped
  up restaurant details from the bottom of the screen on marker tap.

## Some enhancements I ran out of time for

- Show marker as selected on click
- "Go to my location" button
- Composable unit tests
- Cleaner movement between tabs / preservation of state
- "Favorite" button (using Room & an indexed column of favorited Place IDs)
- Query place details & open browser to website on item click

https://user-images.githubusercontent.com/5808931/143809169-fe1aa3c7-6513-449e-84ff-2b5fed1049e9.mp4

