package com.minhhjjj.epicfighttinkercompat.tool.capabilities;

import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.CapabilityItem.ZoomInType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class TCWeaponCapabilityPresets {
	// public static final Function<Item, CapabilityItem.Builder> TC_GREATSWORD = (item) -> {
	// 	WeaponCapability.Builder builder = WeaponCapability.builder()
	// 		.category(WeaponCategories.GREATSWORD)
	// 		.styleProvider((playerpatch) -> Styles.TWO_HAND)
	// 		.collider(ColliderPreset.GREATSWORD)
	// 		.swingSound(EpicFightSounds.WHOOSH_BIG.get())
	// 		.canBePlacedOffhand(false)
	// 		.reach(1.0F)
	// 		.newStyleCombo(Styles.TWO_HAND, ModAnimations.SCYTHE_SLASH1, ModAnimations.SCYTHE_SLASH2, Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH)
	// 		.innateSkill(Styles.TWO_HAND, (itemstack) -> EpicFightSkills.STEEL_WHIRLWIND)
	// 		.livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, ModAnimations.SCYTHE_IDLE)
	// 		.livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, ModAnimations.SCYTHE_WALK)
	// 		.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, ModAnimations.SCYTHE_WALK)
	// 		.livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, ModAnimations.SCYTHE_RUN)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, ModAnimations.SCYTHE_IDLE)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, ModAnimations.SCYTHE_IDLE)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, ModAnimations.SCYTHE_IDLE)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, ModAnimations.SCYTHE_IDLE)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.FLY, ModAnimations.SCYTHE_IDLE)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CREATIVE_FLY, ModAnimations.SCYTHE_IDLE)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.CREATIVE_IDLE, ModAnimations.SCYTHE_IDLE)
	//     	.livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.GREATSWORD_GUARD);
		
	// 	if (item instanceof TieredItem tieredItem) {
	// 		builder.hitSound(tieredItem.getTier() == Tiers.WOOD ? EpicFightSounds.BLUNT_HIT.get() : EpicFightSounds.BLADE_HIT.get());
	// 		builder.hitParticle(tieredItem.getTier() == Tiers.WOOD ? EpicFightParticles.HIT_BLUNT.get() : EpicFightParticles.HIT_BLADE.get());
	// 	}
		
	// 	return builder;
	// };
}