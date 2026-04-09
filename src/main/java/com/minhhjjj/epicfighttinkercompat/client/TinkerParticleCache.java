package com.minhhjjj.epicfighttinkercompat.client;

import net.minecraft.client.particle.Particle;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.List;
import java.util.WeakHashMap;

public class TinkerParticleCache {
    
    public static class TrailData {
        public final LivingEntityPatch<?> owner;
        public final List<ModifierEntry> modifiers;
        public final TrailInfo trailInfo;
        
        public TrailData(LivingEntityPatch<?> owner, List<ModifierEntry> modifiers, TrailInfo trailInfo) {
            this.owner = owner;
            this.modifiers = modifiers;
            this.trailInfo = trailInfo;
        }
    }

    public static final WeakHashMap<Particle, TrailData> ACTIVE_TRAILS = new WeakHashMap<>();
}