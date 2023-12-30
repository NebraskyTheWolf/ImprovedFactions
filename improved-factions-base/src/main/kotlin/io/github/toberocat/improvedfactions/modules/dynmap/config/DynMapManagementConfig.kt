package io.github.toberocat.improvedfactions.modules.dynmap.config

import org.bukkit.configuration.file.FileConfiguration

data class DynMapManagementConfig(
    var isUsingDynMap: Boolean = false
) {
    private val configPath = "factions.power-management"
    fun reload(config: FileConfiguration) {
        isUsingDynMap = config.getBoolean("$configPath.base-member-constant", isUsingDynMap)
    }
}
