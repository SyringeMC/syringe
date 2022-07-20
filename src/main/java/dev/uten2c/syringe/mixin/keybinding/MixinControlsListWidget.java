package dev.uten2c.syringe.mixin.keybinding;

import dev.uten2c.syringe.keybinding.SyringeKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ControlsListWidget.class)
public class MixinControlsListWidget {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;getCategory()Ljava/lang/String;"))
    private String swapSyringeCategoryName(KeyBinding instance) {
        var category = instance.getCategory();
        if (category.equals(SyringeKeyBinding.CATEGORY)) {
            var currentServerEntry = MinecraftClient.getInstance().getCurrentServerEntry();
            var label = currentServerEntry == null ? "Singleplayer" : currentServerEntry.name;
            return "Syringe (" + label + ")";
        }
        return category;
    }
}
