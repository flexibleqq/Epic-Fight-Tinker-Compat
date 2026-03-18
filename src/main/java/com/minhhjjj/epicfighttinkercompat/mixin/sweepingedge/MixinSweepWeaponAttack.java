package com.minhhjjj.epicfighttinkercompat.mixin.sweepingedge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.definition.module.weapon.SweepWeaponAttack; 
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(value = SweepWeaponAttack.class, remap = false) 
public class MixinSweepWeaponAttack {

    @Inject(method = "afterMeleeHit", at = @At("HEAD"), cancellable = true)
    private void cancelSweepInEpicFightMode(IToolStackView tool, ToolAttackContext context, float damage, CallbackInfo ci) {
        LivingEntity attacker = context.getAttacker();
        if (attacker instanceof Player player) {
            PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
            
            if (playerPatch != null && playerPatch.isEpicFightMode()) {
                ci.cancel();
            }
        }
    }
}