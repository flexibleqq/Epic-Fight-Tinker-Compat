package com.minhhjjj.epicfighttinkercompat.mixin.doublejump;

import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import slimeknights.tconstruct.shared.TinkerAttributes;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.client.input.MovementDirection;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.mover.PhantomAscentSkill;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillCastEvent;

@Mixin(value = PhantomAscentSkill.class, remap = false)
public abstract class MixinPhantomAscentSkill {

    @Shadow private int extraJumps;
    @Shadow private double jumpPower;
    @Shadow private List<AnimationAccessor<? extends StaticAnimation>> animations;
    @Shadow private static boolean isJumpActionPressed() { return false; }

    @Inject(method = "onInitiate", at = @At("TAIL"))
    private void replaceMovementListener(SkillContainer container, CallbackInfo ci) {
        PlayerEventListener listener = container.getExecutor().getEventListener();
        UUID EVENT_UUID = UUID.fromString("051a9bb2-7541-11ee-b962-0242ac120002");

        listener.removeListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);

        listener.addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
            if (event.getPlayerPatch().getOriginal().getVehicle() != null || event.getPlayerPatch().getOriginal().getAbilities().flying 
                    || event.getPlayerPatch().isHoldingAny() || event.getPlayerPatch().getEntityState().inaction()) {
                return;
            }
            
            boolean jumpPressed = isJumpActionPressed();
            boolean jumpPressedPrev = container.getDataManager().getDataValue(SkillDataKeys.JUMP_KEY_PRESSED_LAST_TICK.get());
            
            if (jumpPressed && !jumpPressedPrev) {
                if (container.getStack() < 1) {
                    return;
                }
                
                int jumpCounter = container.getDataManager().getDataValue(SkillDataKeys.JUMP_COUNT.get());
                
                int tconExtraJumps = 0;
                var player = event.getPlayerPatch().getOriginal();
                if (player.getAttributes().hasAttribute(TinkerAttributes.JUMP_COUNT.get())) {
                    tconExtraJumps = Mth.floor(player.getAttributeValue(TinkerAttributes.JUMP_COUNT.get())) - 1;
                }
                int totalAllowedJumps = this.extraJumps + Math.max(0, tconExtraJumps);
                
                if (jumpCounter > 0 || event.getPlayerPatch().currentLivingMotion == LivingMotions.FALL) {
                    if (jumpCounter < (totalAllowedJumps + 1)) {
                        SkillCastEvent skillexecuteevent = new SkillCastEvent(container.getExecutor(), container, null);
                        container.getExecutor().getEventListener().triggerEvents(EventType.SKILL_CAST_EVENT, skillexecuteevent);
                        
                        if (skillexecuteevent.isCanceled()) {
                            return;
                        }
                        
                        container.setResource(0.0F);
                        
                        if (jumpCounter == 0 && event.getPlayerPatch().currentLivingMotion == LivingMotions.FALL) {
                            container.getDataManager().setData(SkillDataKeys.JUMP_COUNT.get(), 2);
                        } else {
                            container.getDataManager().setDataF(SkillDataKeys.JUMP_COUNT.get(), (v) -> v + 1);
                        }
                        
                        container.getDataManager().setDataSync(SkillDataKeys.PROTECT_NEXT_FALL.get(), true);
                        
                        float f = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(container.getExecutor().getOriginal()), 0.0F, 1.0F);
                        event.sneakingTick(false, f);

                        final MovementDirection movementDirection = MovementDirection.fromInputState(event.getInputState());
                        final int forward = movementDirection.forward();
                        final int backward = movementDirection.backward();
                        final int left = movementDirection.left();
                        final int right = movementDirection.right();
                        final int vertic = movementDirection.vertical();
                        final int horizon = movementDirection.horizontal();
                        int degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
                        int scale = forward == 0 && backward == 0 && left == 0 && right == 0 ? 0 : (vertic < 0 ? -1 : 1);
                        float launchingYRot = EpicFightCameraAPI.getInstance().getForwardYRot() + degree;
                        Vec3 horizontalLaunchingDirection = MathUtils.getVectorForRotation(0.0F, launchingYRot).scale(0.15D * scale);
                        Vec3 currentDelta = container.getExecutor().getOriginal().getDeltaMovement();
                        Vec3 newDelta = currentDelta.add(horizontalLaunchingDirection);
                        container.getExecutor().getOriginal().setDeltaMovement(newDelta.x, this.jumpPower + container.getExecutor().getOriginal().getJumpBoostPower(), currentDelta.z);
                        event.getPlayerPatch().setModelYRot(EpicFightCameraAPI.getInstance().getForwardYRot() + degree, true);
                        event.getPlayerPatch().playAnimationInClientSide(this.animations.get(vertic < 0 ? 1 : 0), 0.0F);
                        ClientEngine.getInstance().controlEngine.releaseAllServedKeys();
                    };
                } else {
                    container.getDataManager().setData(SkillDataKeys.JUMP_COUNT.get(), 1);
                }
            }
            container.getDataManager().setData(SkillDataKeys.JUMP_KEY_PRESSED_LAST_TICK.get(), jumpPressed);
        });
    }
}