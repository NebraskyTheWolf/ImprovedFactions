package io.github.toberocat.improvedfactions.factions

import io.github.toberocat.improvedfactions.ImprovedFactionsPlugin
import io.github.toberocat.improvedfactions.messages.MessageBroker
import io.github.toberocat.improvedfactions.permissions.Permissions
import io.github.toberocat.improvedfactions.ranks.FactionRank
import io.github.toberocat.improvedfactions.ranks.FactionRankHandler
import io.github.toberocat.improvedfactions.translation.sendLocalized
import io.github.toberocat.improvedfactions.utils.sync
import io.github.toberocat.toberocore.util.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Created: 04.08.2023
 * @author Tobias Madlberger (Tobias)
 */
object FactionHandler {
    fun createFaction(ownerId: UUID, factionName: String): Faction {
        return transaction {
            val faction = Faction.new {
                owner = ownerId
                localName = factionName
                icon = ItemBuilder().title("§e$factionName").material(Material.WOODEN_SWORD)
                    .create(ImprovedFactionsPlugin.instance)
            }
            createListenersFor(faction)

            val ranks = createDefaultRanks(faction.id.value)

            faction.defaultRank = ranks.firstOrNull()?.id?.value ?: FactionRankHandler.guestRankId
            faction.join(ownerId, ranks.lastOrNull()?.id?.value ?: FactionRankHandler.guestRankId)
            return@transaction faction
        }
    }

    fun getAllFaction(): SizedIterable<Faction> {
        return transaction {
            return@transaction Faction.all()
        }
    }

    fun searchFactions(name: String): SizedIterable<Faction> {
        return transaction { return@transaction Faction.find { Factions.name like name } }
    }

    fun createListenersFor(faction: Faction) {
        MessageBroker.listenLocalized(faction.id.value) { message ->
            val members = transaction { faction.members().mapNotNull { Bukkit.getPlayer(it.uniqueId) } }
            sync { members.forEach { it.sendLocalized(message.key, message.placeholders) } }
        }
    }

    private fun createDefaultRanks(factionId: Int): List<FactionRank> {
        val section = ImprovedFactionsPlugin.instance.config.getConfigurationSection("factions.default-faction-ranks")
        if (section == null) {
            ImprovedFactionsPlugin.instance.logger.warning("Couldn't read default faction ranks. Fix it immediately")
            return listOf(
                FactionRankHandler.createRank(
                    factionId, "Member", 1,
                    setOf(Permissions.SEND_INVITES)
                ),
                FactionRankHandler.createRank(
                    factionId, "Owner", 1000,
                    Permissions.knownPermissions.keys
                )
            )
        }

        return section.getKeys(false).map {
            FactionRankHandler.createRank(
                factionId, it, section.getInt("$it.priority"),
                section.getStringList("$it.default-permissions")
            )
        }
    }
}