package io.github.toberocat.improvedfactions.user

import net.runarmc.runarmetricstats.api.RunarMCAPI
import net.runarmc.runarmetricstats.api.permissions.IPermissionsEntity
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * Created: 04.08.2023
 * @author Tobias Madlberger (Tobias)
 */
const val noFactionId = -1

fun OfflinePlayer.factionUser(): FactionUser = uniqueId.factionUser()

fun OfflinePlayer.permissionsUser(): IPermissionsEntity = RunarMCAPI.get().permissionManager.getPlayer(uniqueId)

fun UUID.factionUser(): FactionUser {
    val id = this
    return transaction {
        return@transaction FactionUser.find { FactionUsers.uniqueId eq id }.firstOrNull() ?: FactionUser.new {
            uniqueId = id
            factionId = noFactionId
        }
    }
}
