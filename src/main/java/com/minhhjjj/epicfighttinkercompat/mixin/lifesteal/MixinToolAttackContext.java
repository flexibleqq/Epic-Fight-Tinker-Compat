package com.minhhjjj.epicfighttinkercompat.mixin.lifesteal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;

@Mixin(value = ToolAttackContext.class, remap = false)
public class MixinToolAttackContext {

    @Inject(method = {"isFullyCharged", "isCritical"}, at = @At("HEAD"), cancellable = true)
    private void forceEpicFightConditions(CallbackInfoReturnable<Boolean> cir) {
        ToolAttackContext context = (ToolAttackContext) (Object) this;
        if (context.getAttacker() instanceof Player player) {
            PlayerPatch<?> playerPatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
            if (playerPatch != null && playerPatch.isEpicFightMode()) {
                var animPlayer = playerPatch.getAnimator().getPlayerFor(null);
                if (animPlayer != null && animPlayer.getAnimation() != null && animPlayer.getAnimation().get() instanceof StaticAnimation currentAnim) {
                    AssetAccessor<? extends StaticAnimation> animationAccessor = (AssetAccessor<? extends StaticAnimation>)animPlayer.getAnimation();
                    boolean isBasicAttack = currentAnim.isBasicAttackAnimation() || playerPatch.getAnimator().getVariables().get(BasicAttack.COMBO, animationAccessor).orElse(false);
                    if (!isBasicAttack) {
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
}