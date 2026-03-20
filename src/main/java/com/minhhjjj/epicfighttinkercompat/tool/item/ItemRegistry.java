package com.minhhjjj.epicfighttinkercompat.tool.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "epicfighttinkercompat");

    public static final RegistryObject<Item> STICKY_HANDLE = ITEMS.register("sticky_handle",
            () -> new Item(new Item.Properties())); 

    public static final RegistryObject<Item> WIND_ESSENCE = ITEMS.register("wind_essence",
            () -> new Item(new Item.Properties())); 
}