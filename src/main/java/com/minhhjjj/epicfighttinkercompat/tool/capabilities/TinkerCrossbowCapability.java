package com.minhhjjj.epicfighttinkercompat.tool.capabilities;

import java.util.function.Function;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CrossbowCapability;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.ZoomInType;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;

public class TinkerCrossbowCapability extends CrossbowCapability {
    public static final Function<Item, CapabilityItem.Builder> TCROSSBOW =  (item) -> RangedWeaponCapability.builder()
            .zoomInType(ZoomInType.AIMING)
            .addAnimationsModifier(LivingMotions.IDLE, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.KNEEL, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.WALK, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.RUN, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.SNEAK, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.SWIM, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.FLOAT, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.FALL, Animations.BIPED_HOLD_CROSSBOW)
            .addAnimationsModifier(LivingMotions.RELOAD, Animations.BIPED_CROSSBOW_RELOAD)
            .addAnimationsModifier(LivingMotions.AIM, Animations.BIPED_CROSSBOW_AIM)
            .addAnimationsModifier(LivingMotions.SHOT, Animations.BIPED_CROSSBOW_SHOT)
            .constructor(TinkerCrossbowCapability::new);

    public TinkerCrossbowCapability(CapabilityItem.Builder builder) {
        super(builder);
    }

    @Override
    public LivingMotion getLivingMotion(LivingEntityPatch<?> entitypatch, InteractionHand hand) {
        if (!entitypatch.getEntityState().canUseItem()) {
            return null;
        }
        
        ItemStack heldItem = entitypatch.getOriginal().getMainHandItem();
        if (entitypatch.getOriginal().isUsingItem() && entitypatch.getOriginal().getUseItem() == heldItem) {
            return LivingMotions.RELOAD;
        }

        boolean isCharged = isTinkerCrossbowCharged(heldItem);
        return isCharged ? LivingMotions.AIM : null;
    }

    private boolean isTinkerCrossbowCharged(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.hasTag() && stack.getTag().getBoolean("Charged")) {
            return true;
        }

        if (stack.getItem() instanceof ModifiableCrossbowItem crossbowItem) {
            ToolStack tool = ToolStack.from(stack);
            return tool.getPersistentData().contains(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO, Tag.TAG_COMPOUND);
        }

        return false;
    }
}