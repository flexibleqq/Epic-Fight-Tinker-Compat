package com.minhhjjj.epicfighttinkercompat.mixin.shield;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderShield;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = RenderShield.class, remap = false)
public abstract class MixinRenderShield {

    @Inject(
        method = "renderItemInHand",
        at = @At(
            value = "INVOKE",
            target = "Lyesman/epicfight/api/utils/math/MathUtils;mulStack(Lcom/mojang/blaze3d/vertex/PoseStack;Lyesman/epicfight/api/utils/math/OpenMatrix4f;)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void injectTinkerShieldRender(ItemStack itemstack, LivingEntityPatch<?> entitypatch, InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks, CallbackInfo ci) {

        if (itemstack.getItem().getCreatorModId(itemstack) != null && itemstack.getItem().getCreatorModId(itemstack).equals("tconstruct")) {

            ItemDisplayContext transformType = (hand == InteractionHand.MAIN_HAND) ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            boolean leftHand = !(hand == InteractionHand.MAIN_HAND);
            net.minecraft.client.renderer.entity.ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            BakedModel baseModel = itemRenderer.getItemModelShaper().getItemModel(itemstack);

            BakedModel finalModel = baseModel.getOverrides().resolve(
                baseModel, 
                itemstack, 
                (net.minecraft.client.multiplayer.ClientLevel) entitypatch.getOriginal().level(), 
                entitypatch.getOriginal(), 
                entitypatch.getOriginal().getId()
            );
            
            if (finalModel == null) finalModel = baseModel;

            MultiBufferSource.BufferSource vanillaBuffer = Minecraft.getInstance().renderBuffers().bufferSource();

            poseStack.pushPose();

            baseModel.applyTransform(transformType, poseStack, leftHand);

            itemRenderer.render(
                itemstack,
                ItemDisplayContext.NONE,
                leftHand,
                poseStack,
                vanillaBuffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                finalModel
            );

            vanillaBuffer.endBatch();
            poseStack.popPose();
            poseStack.popPose();
            ci.cancel(); 
        } 
    } 
}