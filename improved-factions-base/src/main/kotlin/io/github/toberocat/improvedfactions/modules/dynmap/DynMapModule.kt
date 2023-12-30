package io.github.toberocat.improvedfactions.modules.dynmap

import io.github.toberocat.improvedfactions.ImprovedFactionsPlugin
import io.github.toberocat.improvedfactions.claims.FactionClaim
import io.github.toberocat.improvedfactions.factions.Faction
import io.github.toberocat.improvedfactions.factions.FactionHandler
import io.github.toberocat.improvedfactions.modules.base.BaseModule
import io.github.toberocat.improvedfactions.modules.dynmap.config.DynMapManagementConfig
import io.github.toberocat.toberocore.command.CommandExecutor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import org.dynmap.DynmapAPI
import org.dynmap.markers.AreaMarker
import org.dynmap.markers.MarkerSet

class DynMapModule : BaseModule {
    override val moduleName = MODULE_NAME

    private val DEF_INFOWINDOW =
        "<div class=\"infowindow\">Area Name: <span style=\"font-weight:bold;\">%owner%</span></div>"
    private val DEF_ADMININFOWINDOW =
        "<div class=\"infowindow\"><span style=\"font-weight:bold;\">SafeZone</span></div>"

    var dynmap: Plugin? = null

    private var api: DynmapAPI? = null
    private var set: MarkerSet? = null

    private val resAreas: Map<String, AreaMarker> = HashMap()


    val config = DynMapManagementConfig()

    override fun onEnable() {
        if (FactionHandler.getAllFaction().empty()) {
            return
        }

        if(dynmap!!.isEnabled)
            activate()
    }

    private fun activate() {
        val markerapi = api?.markerAPI ?: return

        set = markerapi.getMarkerSet("areamanager.markerset")
        if (set == null) set = markerapi.createMarkerSet(
            "areamanager.markerset",
            "Kingdoms",
            null,
            false
        ) else set?.markerSetLabel = "Kingdoms"
        if (set == null) {
            return
        }
        set?.layerPriority = 10
        set?.hideByDefault = false


    }

    private fun handleClaim(faction: FactionClaim, newmap: Map<String, AreaMarker>) {}

    override fun reloadConfig(plugin: ImprovedFactionsPlugin) {
        config.reload(plugin.config)
    }

    override fun addCommands(plugin: ImprovedFactionsPlugin, executor: CommandExecutor) {}

    override fun shouldEnable(plugin: ImprovedFactionsPlugin): Boolean {
        this.dynmap = Bukkit.getPluginManager().getPlugin("dynmap")
        if (this.config.isUsingDynMap) {
            if (dynmap != null) {
                this.api = dynmap as DynmapAPI
                return true
            } else {
                plugin.logger.warning("[DynMapModule] Unable to load DynMap, Aborting..")
            }
        }

        return false
    }

    companion object {
        const val MODULE_NAME = "dynmap"
        fun dynmapModule() =
            (ImprovedFactionsPlugin.modules[MODULE_NAME] as? DynMapModule) ?: throw IllegalStateException()
        fun dynmapPair() = MODULE_NAME to DynMapModule()
    }
}