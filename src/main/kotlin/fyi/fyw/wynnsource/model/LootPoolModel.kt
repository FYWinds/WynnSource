package fyi.fyw.wynnsource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShinyData(
    @SerialName("Item") val item: String,
    @SerialName("Tracker") val tracker: String
)

@Serializable
data class LootPoolDeatil(
    @SerialName("Shiny") var shiny: ShinyData? = null,
    @SerialName("Mythic") val mythic: MutableSet<String> = mutableSetOf(),
    @SerialName("Fabled") val fabled: MutableSet<String> = mutableSetOf(),
    @SerialName("Legendary") val legendary: MutableSet<String> = mutableSetOf(),
    @SerialName("Rare") val rare: MutableSet<String> = mutableSetOf(),
    @SerialName("Unique") val unique: MutableSet<String> = mutableSetOf(),
)

@Serializable
data class LootPoolModel(
    @SerialName("SE") val se: LootPoolDeatil = LootPoolDeatil(),
    @SerialName("Corkus") val corkus: LootPoolDeatil = LootPoolDeatil(),
    @SerialName("Sky") val sky: LootPoolDeatil = LootPoolDeatil(),
    @SerialName("Molten") val molten: LootPoolDeatil = LootPoolDeatil(),
    @SerialName("Canyon") val canyon: LootPoolDeatil = LootPoolDeatil()
)