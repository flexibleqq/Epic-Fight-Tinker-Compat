package com.minhhjjj.epicfighttinkercompat.client.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.client.forgeevent.AnimatedArmorTextureEvent;

@Mod.EventBusSubscriber(modid = "epicfighttinkercompat", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class EpicFightCompatEvents {
    public static boolean hasWarned = false;

    @SubscribeEvent
    public static void onEpicFightGetArmorTexture(AnimatedArmorTextureEvent event) {
        if (ModList.get().isLoaded("epictinkersarmorfix")) {
            if (!hasWarned) {
                com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat.LOGGER.warn("[Epic Fight Tinker Compat] Epic Tinkers Armorfix is no longer needed, as its features are already integrated into this mod");
                hasWarned = true;
            }
            return;
        }
        if (!(event.getItemstack().getItem() instanceof ArmorItem)) {
            return;
        }

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getItemstack().getItem());
        if (itemId != null && itemId.getNamespace().equals("tconstruct")) {
            
            ResourceLocation bakedTexture = ArmorTextureBaker.getOrBakeArmor(
                event.getItemstack(), 
                event.getEquipmentSlot(), 
                event.getLivingEntity().level().registryAccess()
            );

            event.setResultLocation(bakedTexture);
        }
    }
}