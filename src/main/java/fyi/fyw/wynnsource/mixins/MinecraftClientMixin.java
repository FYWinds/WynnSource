package fyi.fyw.wynnsource.mixins;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void setScreenPost(Screen screen, CallbackInfo ci, @Share("oldScreen") LocalRef<Screen> oldScreen) {
        if (screen instanceof HandledScreen<?>) {
            assert interactionManager != null;
            if (interactionManager.hasCreativeInventory() && !(screen instanceof CreativeInventoryScreen)) {
                return;
            }
            // container (9xn)
            if (screen instanceof GenericContainerScreen) {
                // No longer needed, already handled in PacketListenerMixin
//                RewardPoolCollector.INSTANCE.onContainerScreen((GenericContainerScreen) screen);
            }
        }
    }
}
