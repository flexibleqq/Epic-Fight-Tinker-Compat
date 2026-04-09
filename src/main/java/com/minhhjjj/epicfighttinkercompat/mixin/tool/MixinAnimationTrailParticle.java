package com.minhhjjj.epicfighttinkercompat.mixin.tool;

import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.particle.AnimationTrailParticle;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import com.minhhjjj.epicfighttinkercompat.client.TinkerParticleCache;

@Mixin(value = AnimationTrailParticle.class, remap = false)
public abstract class MixinAnimationTrailParticle {

    @Shadow protected Joint joint;

    @Inject(
        method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/Joint;Lyesman/epicfight/api/asset/AssetAccessor;Lyesman/epicfight/api/client/animation/property/TrailInfo;)V", 
        at = @At("RETURN")
    )
    private void cacheModifiersOnInit(ClientLevel level, LivingEntityPatch<?> owner, Joint joint, AssetAccessor<? extends StaticAnimation> animation, TrailInfo trailInfo, CallbackInfo ci) {
        if (owner != null && owner.getOriginal() != null) {
            InteractionHand hand = trailInfo.hand() != null ? trailInfo.hand() : InteractionHand.MAIN_HAND;
            ItemStack stack = owner.getOriginal().getItemInHand(hand);

            if (!stack.isEmpty() && stack.getItem().getCreatorModId(stack) != null && stack.getItem().getCreatorModId(stack).equals("tconstruct")) {
                ToolStack tool = ToolStack.from(stack);
                if (!tool.isBroken() && tool.getModifierList().size() > 0) {
                    TinkerParticleCache.ACTIVE_TRAILS.put((Particle)(Object)this, new TinkerParticleCache.TrailData(owner, tool.getModifierList(), trailInfo));
                }
            }
        }
    }

    @Inject(method = "createNextCurve", at = @At("TAIL"))
    private void spawnParticlesOnBlade(CallbackInfo ci) {
        TinkerParticleCache.TrailData data = TinkerParticleCache.ACTIVE_TRAILS.get((Particle)(Object)this);
        
        if (data == null || data.owner == null || data.owner.getOriginal() == null) return;

        ClientLevel level = (ClientLevel) data.owner.getOriginal().level();

        Pose currentPose = data.owner.getAnimator().getPose(1.0F);
        Vec3 posCur = data.owner.getOriginal().getPosition(1.0F);
        
        OpenMatrix4f curModelTf = OpenMatrix4f.createTranslation((float)posCur.x, (float)posCur.y, (float)posCur.z)
            .rotateDeg(180.0F, Vec3f.Y_AXIS)
            .mulBack(data.owner.getModelMatrix(1.0F));
            
        OpenMatrix4f currentJointTf = data.owner.getArmature().getBoundTransformFor(currentPose, this.joint).mulFront(curModelTf);
        
        Vec3 startPos = OpenMatrix4f.transform(currentJointTf, data.trailInfo.start());
        Vec3 endPos = OpenMatrix4f.transform(currentJointTf, data.trailInfo.end());

        for (ModifierEntry modEntry : data.modifiers) {
            String modId = modEntry.getId().toString();
            int modLevel = modEntry.getLevel(); 

            for (int i = 0; i < modLevel; ++i) {
                float lerp = level.random.nextFloat();
                
                double px = startPos.x + (endPos.x - startPos.x) * lerp;
                double py = startPos.y + (endPos.y - startPos.y) * lerp;
                double pz = startPos.z + (endPos.z - startPos.z) * lerp;
                
                px += (level.random.nextDouble() - 0.5) * 0.3;
                py += (level.random.nextDouble() - 0.5) * 0.3;
                pz += (level.random.nextDouble() - 0.5) * 0.3;

                if (modId.equals("tconstruct:fiery")) {
                    level.addParticle(ParticleTypes.FLAME, px, py, pz, 0, 0.05, 0);
                } 
                else if (modId.equals("tconstruct:freezing")) {
                    level.addParticle(ParticleTypes.SNOWFLAKE, px, py, pz, 0, -0.02, 0);
                } 
                else if (modId.equals("tconstruct:necrotic")) {
                    level.addParticle(ParticleTypes.SOUL, px, py, pz, 0, 0.05, 0);
                } 
                else if (modId.equals("tconstruct:venom") || modId.equals("tconstruct:antitoxin")) {
                    level.addParticle(ParticleTypes.ENTITY_EFFECT, px, py, pz, 0.2, 0.8, 0.2);
                }
                else if (modId.equals("tconstruct:enderporting") || modId.equals("tconstruct:enderference")) {
                    level.addParticle(ParticleTypes.PORTAL, px, py, pz, (level.random.nextDouble() - 0.5) * 2.0, (level.random.nextDouble() - 0.5) * 2.0, (level.random.nextDouble() - 0.5) * 2.0);
                }
            }
        }
    }
}