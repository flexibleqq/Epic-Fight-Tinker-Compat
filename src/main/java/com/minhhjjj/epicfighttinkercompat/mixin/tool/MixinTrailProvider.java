package com.minhhjjj.epicfighttinkercompat.mixin.tool; 

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import java.util.Optional;

@Mixin(value = AnimationTrailParticle.Provider.class, remap = false)
public class MixinTrailProvider {

    @ModifyArg(
        method = "createParticle",
        at = @At(
            value = "INVOKE",
            target = "Lyesman/epicfight/client/particle/AnimationTrailParticle;<init>(Lnet/minecraft/client/multiplayer/ClientLevel;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/Joint;Lyesman/epicfight/api/asset/AssetAccessor;Lyesman/epicfight/api/client/animation/property/TrailInfo;)V"
        ),
        index = 4 
    )
    private TrailInfo modifyTinkerTrailArg(ClientLevel level, LivingEntityPatch<?> owner, Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo originalTrailInfo) {
        
        InteractionHand hand = originalTrailInfo.hand() != null ? originalTrailInfo.hand() : InteractionHand.MAIN_HAND;
        ItemStack stack = owner.getOriginal().getItemInHand(hand);

        if (stack.isEmpty()) return originalTrailInfo;

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());

        if (itemId != null && itemId.getNamespace().equals("tconstruct")) {
            int tintColor = 0xFFFFFF;
            try {
                ToolStack tool = ToolStack.from(stack);
                if (!tool.isBroken() && tool.getMaterials().size() > 0) {
                    MaterialVariantId variantId = tool.getMaterials().get(0).getVariant();
                    Optional<MaterialRenderInfo> renderInfoOpt = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(variantId);
                    
                    if (renderInfoOpt.isPresent()) {
                        tintColor = renderInfoOpt.get().vertexColor();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            float r = ((tintColor >> 16) & 0xFF) / 255.0f;
            float g = ((tintColor >> 8)  & 0xFF) / 255.0f;
            float b = ( tintColor        & 0xFF) / 255.0f;

            return originalTrailInfo.unpackAsBuilder()
                .r(r).g(g).b(b)
                .create();
        }
        
        return originalTrailInfo;
    }
}