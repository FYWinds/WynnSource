package fyi.fyw.wynnsource.mixins;

import com.wynntils.models.aspects.type.AspectInfo;
import com.wynntils.models.items.items.game.AspectItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AspectItem.class, remap = false)
public interface AspectInfoAccessor {
    @Accessor(value = "aspectInfo", remap = false)
    AspectInfo getAspectInfo();
}
