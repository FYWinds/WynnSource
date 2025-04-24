package fyi.fyw.wynnsource.module

import com.wynntils.core.components.Models
import com.wynntils.models.gear.type.GearTier
import com.wynntils.models.items.WynnItem
import com.wynntils.models.items.WynnItemData
import com.wynntils.models.items.items.game.AspectItem
import com.wynntils.models.items.items.game.GearItem
import com.wynntils.models.items.items.game.InsulatorItem
import com.wynntils.models.items.items.game.SimulatorItem
import com.wynntils.models.stats.type.ShinyStatType
import com.wynntils.utils.mc.LoreUtils
import fyi.fyw.wynnsource.WynnSourceEntry
import fyi.fyw.wynnsource.mixins.AspectInfoAccessor
import fyi.fyw.wynnsource.mixins.ShinyStatTypesAccessor
import fyi.fyw.wynnsource.model.LootPoolModel
import fyi.fyw.wynnsource.model.RaidPoolModel
import fyi.fyw.wynnsource.model.ShinyData
import fyi.fyw.wynnsource.utils.JsonUtils
import fyi.fyw.wynnsource.utils.StringUtils
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text


object RewardPoolCollector {
    // Temporary magic number marking the starting slot of reward
    private const val LOOT_REWARD_START_SLOT = 18
    private const val RAID_REWARD_START_SLOT = 27

    private var lootPoolData = LootPoolModel()
    private var raidPoolData = RaidPoolModel()

    fun onContainerScreen(screen: GenericContainerScreen) {
        if (!WynnSourceEntry.CONFIG.enabled() || !WynnSourceEntry.CONFIG.collectRewardPool()) return;

        val screenHandler = screen.screenHandler
        val inventory = screenHandler.inventory
        val (invType, invId) = determineInventory(screen.title)
        if (invType == null || invId == null) {
            return
        }

//        println("Inventory Type: $invType, Inventory ID: $invId")

        val items = extractItemList(inventory, invType)


        when (invType) {
            RewardPoolType.Loot -> {
                val poolDetail = when (invId) {
                    "Canyon" -> lootPoolData::canyon
                    "Corkus" -> lootPoolData::corkus
                    "Molten" -> lootPoolData::molten
                    "Sky" -> lootPoolData::sky
                    "SE" -> lootPoolData::se
                    else -> throw IllegalStateException("Unreachable when case")
                }.get()

                items.asSequence().filterNotNull().forEach { wynnItem ->
                    when (wynnItem) {
                        is GearItem -> {
                            when (wynnItem.gearTier) {
                                GearTier.MYTHIC -> {
                                    val itemStack: ItemStack = wynnItem.data.get(WynnItemData.ITEMSTACK_KEY)
                                    val shinyStatType = extractShiny(itemStack)
                                    if (shinyStatType != null) {
                                        poolDetail.shiny = ShinyData(
                                            wynnItem.name,
                                            shinyStatType.displayName
                                        )
                                    } else {
                                        poolDetail.mythic.add(wynnItem.name)
                                    }
                                }

                                GearTier.FABLED -> poolDetail.fabled.add(wynnItem.name)

                                GearTier.LEGENDARY -> poolDetail.legendary.add(wynnItem.name)

                                GearTier.RARE -> poolDetail.rare.add(wynnItem.name)

                                GearTier.UNIQUE -> poolDetail.unique.add(wynnItem.name)

                                else -> throw IllegalStateException("Unreachable when case")
                            }
                        }

                        is SimulatorItem -> {
                            poolDetail.mythic.add("Corkian Simulator")
                        }

                        is InsulatorItem -> {
                            poolDetail.mythic.add("Corkian Insulator")
                        }

                        else -> {}
                    }

                }
            }

            RewardPoolType.Raid -> {
                val poolDetail = when (invId) {
                    "NOTG" -> raidPoolData::notg
                    "NOL" -> raidPoolData::nol
                    "TCC" -> raidPoolData::tcc
                    "TNA" -> raidPoolData::tna
                    else -> throw IllegalStateException("Unreachable when case")
                }.get()

                items.asSequence().filterNotNull().forEach { wynnItem ->
                    when (wynnItem) {
                        is AspectItem -> {
                            when (wynnItem.gearTier) {
                                GearTier.MYTHIC -> poolDetail.mythic.add((wynnItem as AspectInfoAccessor).aspectInfo.name)

                                GearTier.FABLED -> poolDetail.fabled.add((wynnItem as AspectInfoAccessor).aspectInfo.name)

                                GearTier.LEGENDARY -> poolDetail.legendary.add((wynnItem as AspectInfoAccessor).aspectInfo.name)

                                else -> throw IllegalStateException("Unreachable when case")
                            }
                        }

                        else -> {}
                    }

                }
            }
        }

        JsonUtils.writeToFile(
            FabricLoader.getInstance().gameDir.resolve("${WynnSourceEntry.MOD_ID}/RewardPool-Loot.json"),
            lootPoolData
        )
        JsonUtils.writeToFile(
            FabricLoader.getInstance().gameDir.resolve("${WynnSourceEntry.MOD_ID}/RewardPool-Raid.json"),
            raidPoolData
        )
    }

    private fun extractItemList(inventory: Inventory, invType: RewardPoolType): List<WynnItem?> {
        return when (invType) {
            RewardPoolType.Loot -> {
                (LOOT_REWARD_START_SLOT..<inventory.size()).map {
                    val item = inventory.getStack(it)
                    if (item.isEmpty) {
                        null
                    } else {
                        Models.Item.getWynnItem(item).get()
                    }
                }
            }

            RewardPoolType.Raid -> {
                (RAID_REWARD_START_SLOT..<inventory.size()).map {
                    val item = inventory.getStack(it)
                    if (item.isEmpty) {
                        null
                    } else {
                        Models.Item.getWynnItem(item).get()
                    }
                }
            }
        }
    }

    private fun determineInventory(title: Text): Pair<RewardPoolType?, String?> {
        val cps = StringUtils.splitByCodePoint(title.string)
        if (cps.size != 4) {
            return Pair(null, null)
        }

        val invType = when (cps[1]) {
            RewardPoolType.Loot.char -> RewardPoolType.Loot
            RewardPoolType.Raid.char -> RewardPoolType.Raid
            else -> null
        }

        val invId = when (invType) {
            RewardPoolType.Loot -> LootPool.entries.find { it.char == cps[3] }?.name ?: ""
            RewardPoolType.Raid -> RaidPool.entries.find { it.char == cps[3] }?.name ?: ""
            else -> null
        }

        return Pair(invType, invId)
    }


    private fun extractShiny(item: ItemStack): ShinyStatType? {
        if (item.customName?.string?.contains("Shiny") != true) {
            return null;
        }
        val lore = LoreUtils.getTooltipLines(item)
        return (Models.Shiny as ShinyStatTypesAccessor).shinyStatTypes.values.firstOrNull { shinyStatType ->
            lore.any { text -> text.string.contains(shinyStatType.displayName) }
        }
    }


    enum class RewardPoolType(val char: String) {
        Loot("\uE00A"),
        Raid("\uE00D"),
    }

    enum class LootPool(val char: String) {
        Canyon(char = "\uF006"),
        Corkus(char = "\uF007"),
        Molten(char = "\uF008"),
        Sky(char = "\uF009"),
        SE(char = "\uF00A"),
    }

    enum class RaidPool(val char: String) {
        NOTG(char = "\uF00B"),
        NOL(char = "\uF00C"),
        TCC(char = "\uF00D"),
        TNA(char = "\uF00E"),
    }
}
