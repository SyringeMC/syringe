package dev.uten2c.syringe.mixin.item;

import dev.uten2c.syringe.util.ItemFlag;
import dev.uten2c.syringe.util.ItemStackUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    private static final Int2ObjectOpenHashMap<ItemStack> EMPTY_MAP = new Int2ObjectOpenHashMap<>();

    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void cancelClick(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        var screenHandler = player.currentScreenHandler;
        if (slotId < 0) {
            return;
        }
        var stack = screenHandler.getSlot(slotId).getStack();
        if (ItemStackUtils.getFlag(stack, ItemFlag.CAN_NOT_INVENTORY_MOVE)) {
            networkHandler.sendPacket(new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), slotId, button, actionType, screenHandler.getCursorStack().copy(), EMPTY_MAP));
            ci.cancel();
        }
    }
}
