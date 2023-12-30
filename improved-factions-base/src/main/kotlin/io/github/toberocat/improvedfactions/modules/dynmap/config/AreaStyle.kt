package io.github.toberocat.improvedfactions.modules.dynmap.config

data class AreaStyle(
    var strokeColor: String = "#33FFD7",
    var strokeOpacity: Double = 0.8,
    var strokeWeight: Int = 3,
    var fillColor: String = "#33FCFF",
    var fillOpacity: Double = 0.33,
    var label: String = "",
)
