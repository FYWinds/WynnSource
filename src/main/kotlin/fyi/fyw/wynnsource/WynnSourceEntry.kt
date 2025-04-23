package fyi.fyw.wynnsource


import fyi.fyw.wynnsource.config.WynnSourceConfig
import net.fabricmc.api.ClientModInitializer

@Suppress("UNUSED")
object WynnSourceEntry : ClientModInitializer {
    const val MOD_ID = "wynnsource"

    val CONFIG: WynnSourceConfig = WynnSourceConfig.createAndLoad()

    override fun onInitializeClient() {
    }
}