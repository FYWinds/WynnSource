package fyi.fyw.wynnsource.utils

import net.minecraft.inventory.Inventory


fun Inventory.isEmpty(): Boolean {
    for (i in 0 until size()) {
        if (!getStack(i).isEmpty) {
            return false
        }
    }
    return true
}