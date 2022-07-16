package dev.uten2c.syringe.mixin.item;

import dev.uten2c.syringe.util.ItemStackUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class MixinHeldItemFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinHeldItemFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Redirect(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void swapThirdPersonStack(HeldItemRenderer instance, LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        var nbt = new NbtCompound();
        stack.writeNbt(nbt);
        var thirdPersonStack = ItemStackUtils.getThirdPersonStack(stack);
        var resultStack = thirdPersonStack == null ? stack : thirdPersonStack;
        instance.renderItem(entity, resultStack, renderMode, leftHanded, matrices, vertexConsumers, light);
    }
}
