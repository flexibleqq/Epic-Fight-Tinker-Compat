package com.minhhjjj.epicfighttinkercompat.tool;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;
import com.minhhjjj.epicfighttinkercompat.tool.capabilities.TinkerWeaponCapabilityProvider;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mod.EventBusSubscriber(modid = EpicFightTinkerCompat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponCapabilityAttacher {

    @SubscribeEvent
    public static void onAttachCapabilty(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (!(stack.getItem() instanceof IModifiable)) return;
        ToolStack tool = ToolStack.from(stack);

        String id = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        String weaponType = id.substring(11);
        if (weaponType.isEmpty()) return;

        event.addCapability(TinkerWeaponCapabilityProvider.EPIC_CAP_ID, new TinkerWeaponCapabilityProvider(weaponType, tool));
    }
}
