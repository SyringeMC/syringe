package dev.uten2c.syringe.mixin.item;

import dev.uten2c.syringe.util.ItemFlag;
import dev.uten2c.syringe.util.ItemStackUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixinAbstractBlockState {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        var stack = player.getStackInHand(hand);
        if (ItemStackUtils.getFlag(stack, ItemFlag.CAN_NOT_INTERACT_WITH_BLOCK)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
