package fyi.fyw.wynnsource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RaidPoolDetail(
    @SerialName("Mythic") val mythic: MutableSet<String> = mutableSetOf(),
    @SerialName("Fabled") val fabled: MutableSet<String> = mutableSetOf(),
    @SerialName("Legendary") val legendary: MutableSet<String> = mutableSetOf()
)

@Serializable
data class RaidPoolModel(
    @SerialName("TNA") val tna: RaidPoolDetail = RaidPoolDetail(),
    @SerialName("TCC") val tcc: RaidPoolDetail = RaidPoolDetail(),
    @SerialName("NOL") val nol: RaidPoolDetail = RaidPoolDetail(),
    @SerialName("NOTG") val notg: RaidPoolDetail = RaidPoolDetail()
)