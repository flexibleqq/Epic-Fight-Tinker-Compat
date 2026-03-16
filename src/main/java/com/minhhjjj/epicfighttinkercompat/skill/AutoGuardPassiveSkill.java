package com.minhhjjj.epicfighttinkercompat.skill;

import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent.ModRegistryWorker;import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@Mod.EventBusSubscriber(modid = "epicfighttinkercompat", bus = Mod.EventBusSubscriber.Bus.MOD)
public class AutoGuardPassiveSkill extends PassiveSkill {
    public static Skill AUTO_GUARD_PASSIVE;

    @SubscribeEvent
    public static void buildSkillEvent(SkillBuildEvent build) {
        ModRegistryWorker modRegistry = build.createRegistryWorker("epicfighttinkercompat");
        AUTO_GUARD_PASSIVE = modRegistry.build("auto_guard", AutoGuardPassiveSkill::new, PassiveSkill.createPassiveBuilder().setCategory(SkillCategories.WEAPON_PASSIVE));
    }

    private static final UUID EVENT_UUID = UUID.fromString("12345678-abcd-1234-abcd-123456789abc"); 

    public AutoGuardPassiveSkill(SkillBuilder<? extends PassiveSkill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        
        container.getExecutor().getEventListener().addEventListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, (event) -> {
            int phaseLevel = event.getPlayerPatch().getEntityState().getLevel();
            
            if (event.getDamage() > 0.0F && phaseLevel > 0 && phaseLevel < 3 && isBlockableSource(event.getDamageSource())) {
                DamageSource damageSource = event.getDamageSource();
                boolean isFront = false;
                Vec3 sourceLocation = damageSource.getSourcePosition();
                
                if (sourceLocation != null) {
                    Vec3 viewVector = event.getPlayerPatch().getOriginal().getViewVector(1.0F);
                    Vec3 toSourceLocation = sourceLocation.subtract(event.getPlayerPatch().getOriginal().position()).normalize();
                    
                    if (toSourceLocation.dot(viewVector) > 0.0D) {
                        isFront = true;
                    }
                }
                
                if (isFront) {
                    event.getPlayerPatch().playSound(EpicFightSounds.BLUNT_HIT_HARD.get(), -0.05F, 0.1F);
                    ServerPlayer playerentity = event.getPlayerPatch().getOriginal();
                    EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(playerentity.serverLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, playerentity, damageSource.getDirectEntity());

                    float knockback = 0.25F;
                    if (damageSource instanceof EpicFightDamageSource epicfightSource) {
                        knockback += Math.min(epicfightSource.calculateImpact() * 0.1F, 1.0F);
                    }
                    if (damageSource.getDirectEntity() instanceof LivingEntity livingentity) {
                        knockback += EnchantmentHelper.getKnockbackBonus(livingentity) * 0.1F;
                    }
                    
                    EpicFightCapabilities.getUnparameterizedEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class).ifPresent(attackerpatch -> {
                        attackerpatch.setLastAttackEntity(event.getPlayerPatch().getOriginal());
                    });
                    
                    event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);
                    event.setCanceled(true);
                    event.setResult(AttackResult.ResultType.BLOCKED);
                }
            }
        }, 0);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, 0);
    }

    private static boolean isBlockableSource(DamageSource damageSource) {
        return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.is(DamageTypeTags.IS_EXPLOSION);
    }
}