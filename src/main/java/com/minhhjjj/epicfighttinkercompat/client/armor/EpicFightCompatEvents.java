package com.minhhjjj.epicfighttinkercompat.client.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
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

        if (TinkerArmorExtractor.supportsTinkerArmorRendering(event.getItemstack())) {
            var bakedTexture = ArmorTextureBaker.getOrBakeArmor(
                event.getItemstack(),
                event.getEquipmentSlot(),
                event.getLivingEntity().level().registryAccess()
            );

            if (bakedTexture != null) {
            event.setResultLocation(bakedTexture);
            }
        }
    }
}
