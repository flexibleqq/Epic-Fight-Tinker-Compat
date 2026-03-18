package com.minhhjjj.epicfighttinkercompat.mixin.doublejump;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.tools.logic.DoubleJumpHandler;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.mover.PhantomAscentSkill;

@Mixin(value = DoubleJumpHandler.class, remap = false)
public class MixinDoubleJumpHandler {

    @Inject(method = "extraJump", at = @At("HEAD"), cancellable = true)
    private static void cancelJumpIfHasSkill(Player entity, CallbackInfoReturnable<Boolean> cir) {
        PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
        
        if (playerPatch != null) {
            boolean hasPhantomAscent = false;
            
            if (playerPatch.getSkillCapability() != null && playerPatch.getSkillCapability().skillContainers != null) {
                for (SkillContainer container : playerPatch.getSkillCapability().skillContainers) {
                    if (container != null && container.getSkill() instanceof PhantomAscentSkill) {
                        hasPhantomAscent = true;
                        break;
                    }
                }
            }
            
            if (hasPhantomAscent) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "extraJump", at = @At("RETURN"))
    private static void playAnimationIfNoSkill(Player entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() != null && cir.getReturnValue()) {
            PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
            
            if (playerPatch != null) {
                if (entity.getCommandSenderWorld().isClientSide) {
                    playerPatch.playAnimationInClientSide(Animations.BIPED_PHANTOM_ASCENT_FORWARD, 0.0F);
                } else {
                    playerPatch.playAnimationSynchronized(Animations.BIPED_PHANTOM_ASCENT_FORWARD, 0.0F);
                }
            }
        }
    }
}