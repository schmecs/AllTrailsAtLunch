package com.rebeccablum.alltrailsatlunch

import com.google.android.gms.maps.model.LatLng
import com.rebeccablum.alltrailsatlunch.data.Geometry
import com.rebeccablum.alltrailsatlunch.data.Place
import com.rebeccablum.alltrailsatlunch.models.Restaurant

const val NETWORK_ERROR = "network"
const val GENERIC_ERROR = "generic"

val LAT_LNG_1 = LatLng(41.1234, 73.1234)
val LAT_LNG_2 = LatLng(41.1235, 73.1235)

val PLACE_1 = Place(
    "1", "123 Sesame Street", "800-555-5555", Geometry(
        com.rebeccablum.alltrailsatlunch.data.LatLng(LAT_LNG_1.latitude, LAT_LNG_1.longitude)
    ), "Mike's"
)
val PLACE_2 = Place(
    "2", "14 Elm Street", "123-456-7890", Geometry(
        com.rebeccablum.alltrailsatlunch.data.LatLng(
            LAT_LNG_2.latitude, LAT_LNG_2.longitude
        )
    ), "Anne's"
)

val RESTAURANT_1 = Restaurant("1", "Mike's", location = LAT_LNG_1)
val RESTAURANT_2 = Restaurant("2", "Anne's", location = LAT_LNG_2)

val SEARCH_TEXT = "italian"