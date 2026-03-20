package com.minhhjjj.epicfighttinkercompat.tool.capabilities;

import java.util.function.Function;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.sounds.SoundEvent;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.gameasset.EpicFightSkills;

import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;

public class JavelinCapability extends RangedWeaponCapability {
    public static final Function<Item, CapabilityItem.Builder> JAVELIN =  (item) -> {
        CapabilityItem.Builder builder = JavelinCapability.builder()
            .zoomInType(ZoomInType.USE_TICK)
            .addAnimationsModifier(LivingMotions.IDLE, Animations.BIPED_IDLE)
            .addAnimationsModifier(LivingMotions.WALK, Animations.BIPED_WALK)
            .addAnimationsModifier(LivingMotions.AIM, Animations.BIPED_JAVELIN_AIM)
            .addAnimationsModifier(LivingMotions.SHOT, Animations.BIPED_JAVELIN_THROW)
            .constructor(JavelinCapability::new)
            .collider(ColliderPreset.SPEAR)
            .category(WeaponCategories.TRIDENT);

        return builder;
    };

    private List<AnimationAccessor<? extends AttackAnimation>> tridentAttackMotion;
    private List<AnimationAccessor<? extends AttackAnimation>> spearOneHandAttackMotion;
    private List<AnimationAccessor<? extends AttackAnimation>> spearTwoHandAttackMotion;
    private List<AnimationAccessor<? extends AttackAnimation>> mountAttackMotion;
    
    public JavelinCapability(CapabilityItem.Builder builder) {
        super(builder);
        
        this.tridentAttackMotion = List.of(Animations.TRIDENT_AUTO1, Animations.TRIDENT_AUTO2, Animations.TRIDENT_AUTO3, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH);
        this.spearOneHandAttackMotion = List.of(Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH);
        this.spearTwoHandAttackMotion = List.of(Animations.SPEAR_TWOHAND_AUTO1, Animations.SPEAR_TWOHAND_AUTO2, Animations.SPEAR_DASH, Animations.SPEAR_TWOHAND_AIR_SLASH);
        this.mountAttackMotion = List.of(Animations.SPEAR_MOUNT_ATTACK);
    }
    
    @Override
    public Style getStyle(LivingEntityPatch<?> entitypatch) {
        ItemStack stack = entitypatch.getOriginal().getMainHandItem();
        if (!(stack.getItem() instanceof IModifiable)) return Styles.COMMON;
        ToolStack tool = ToolStack.from(stack);
        if (tool == null) return Styles.COMMON;
        ModifierId spearyId = new ModifierId(EpicFightTinkerCompat.MODID, "speary");
        
        if (tool.getModifierLevel(spearyId) > 0) {
            if (entitypatch.getOriginal().getOffhandItem().isEmpty()) {
                return Styles.TWO_HAND;
            } else {
                return Styles.ONE_HAND;
            }
        }
        
        return Styles.ONE_HAND;
    }
    
    @Override
    public SoundEvent getHitSound() {
        return EpicFightSounds.BLADE_HIT.get();
    }
    
    @Override
    public HitParticleType getHitParticle() {
        return EpicFightParticles.HIT_BLADE.get();
    }
    
    @Override
    public List<AnimationAccessor<? extends AttackAnimation>> getAutoAttackMotion(PlayerPatch<?> playerpatch) {
        ItemStack stack = playerpatch.getOriginal().getMainHandItem();
        if (!(stack.getItem() instanceof IModifiable)) return this.tridentAttackMotion;
        ToolStack tool = ToolStack.from(stack);
        if (tool == null) return this.tridentAttackMotion;
        ModifierId spearyId = new ModifierId(EpicFightTinkerCompat.MODID, "speary");

        if (tool.getModifierLevel(spearyId) > 0) {
            Style currentStyle = this.getStyle(playerpatch);
            
            if (currentStyle == Styles.TWO_HAND) {
                return this.spearTwoHandAttackMotion;
            } else {
                return this.spearOneHandAttackMotion;
            }
        }
        
        return this.tridentAttackMotion;
    }

    @Override
    public LivingMotion getLivingMotion(LivingEntityPatch<?> entitypatch, InteractionHand hand) {
        return entitypatch.getOriginal().isUsingItem() && entitypatch.getOriginal().getUseItem().getUseAnimation() == UseAnim.SPEAR ? LivingMotions.AIM : null;
    }

    @Nullable
    @Override
    public Skill getInnateSkill(PlayerPatch<?> playerpatch, ItemStack itemstack) {
        Style currentStyle = this.getStyle(playerpatch);
        ToolStack tool = ToolStack.from(itemstack);
        if (tool == null) return null;
        ModifierId spearyId = new ModifierId(EpicFightTinkerCompat.MODID, "speary");
        ModifierId returningId = new ModifierId(EpicFightTinkerCompat.MODID, "returning");

        if (tool.getModifierLevel(spearyId) > 0) {
            if (currentStyle == Styles.TWO_HAND) {
                return EpicFightSkills.GRASPING_SPIRE;
            }
            else if (currentStyle == Styles.ONE_HAND) {
                return EpicFightSkills.HEARTPIERCER;
            }
        } else if (tool.getModifierLevel(returningId) > 0) {
            return EpicFightSkills.EVERLASTING_ALLEGIANCE;
        }
        return null;
    }
}