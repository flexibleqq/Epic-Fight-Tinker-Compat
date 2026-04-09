package com.minhhjjj.epicfighttinkercompat.mixin.tool;

import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import yesman.epicfight.api.client.model.ItemSkinsReloadListener;

@Mixin(value = ItemSkinsReloadListener.class, remap = false)
public class MixinItemSkinsReloadListener {

    @Inject(method = {"apply", "m_5787_"}, at = @At("HEAD"), remap = false)
    private void injectTinkerSkins(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        
        addTinkerWeapon(map, "sword", 0.0, 0.1, -0.1, 0.0, -0.05, -0.95);
        addTinkerWeapon(map, "cleaver", 0.0, 0.0, -0.2, 0.0, -0.36, -2.0, 20);
        addTinkerWeapon(map, "dagger", 0.0, 0.0, -0.2, 0.0, 0.1, -0.7);
        addTinkerWeapon(map, "scythe", 0.0, 0.0, -0.2, 0.0, -0.24, -1.8, 20);
        addTinkerWeapon(map, "sledge_hammer", 0.0, 0.0, -0.7, 0.0, -0.36, -2.0, 20);
        addTinkerWeapon(map, "javelin", 0.0, 0.2, -0.9, 0.0, 0.25, -2.0, 20);
        addTinkerWeapon(map, "axe", 0.0, -0.15, -0.35, 0.0, -0.15, -0.7);
        addTinkerWeapon(map, "broad_axe", 0.0, -0.23, -0.53, 0.0, -0.23, -1.35, 20);
    }


    private void addTinkerWeapon(Map<ResourceLocation, JsonElement> map, String name, double startX, double startY, double startZ, double endX, double endY, double endZ, int lifetime) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("tconstruct", name);

        if (!map.containsKey(id)) {
            JsonObject root = new JsonObject();
            JsonObject trail = new JsonObject();

            JsonArray begin = new JsonArray();
            begin.add(startX); begin.add(startY); begin.add(startZ);
            
            JsonArray end = new JsonArray();
            end.add(endX); end.add(endY); end.add(endZ);

            trail.add("begin_pos", begin);
            trail.add("end_pos", end);
            
            trail.addProperty("texture_path", "epicfight:textures/particle/swing_trail.png");
            trail.addProperty("particle_type", "epicfight:swing_trail");
            trail.addProperty("lifetime", lifetime);
            trail.addProperty("interpolations", 10);

            root.add("trail", trail);

            map.put(id, root);
        }
    }
    private void addTinkerWeapon(Map<ResourceLocation, JsonElement> map, String name, double startX, double startY, double startZ, double endX, double endY, double endZ) {
        addTinkerWeapon(map, name, startX, startY, startZ, endX, endY, endZ, 10);
    }
}