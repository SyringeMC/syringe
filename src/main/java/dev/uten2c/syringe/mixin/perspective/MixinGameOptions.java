package dev.uten2c.syringe.mixin.perspective;

import dev.uten2c.syringe.SyringeMod;
import dev.uten2c.syringe.util.ExtendedGameOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class MixinGameOptions implements ExtendedGameOptions {
    @Shadow
    private Perspective perspective;

    @Inject(method = "setPerspective", at = @At("HEAD"), cancellable = true)
    private void setPerspective(Perspective perspective, CallbackInfo ci) {
        setPerspective(perspective, false);
        ci.cancel();
    }

    @Override
    public void setPerspective(Perspective perspective, boolean force) {
        if (!force && SyringeMod.isPerspectiveLocked) {
            var player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
            }
            return;
        }
        this.perspective = perspective;
    }
}
