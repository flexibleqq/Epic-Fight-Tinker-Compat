package com.minhhjjj.epicfighttinkercompat;

import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class EpicFightModifier {
    private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(EpicFightTinkerCompat.MODID);
    public static final StaticModifier<Modifier> EPIC_STATS = MODIFIERS.register("epic_stats", Modifier::new);

    public static void register(IEventBus modEventBus) {
        MODIFIERS.register(modEventBus);
    }
}