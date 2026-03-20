package com.minhhjjj.epicfighttinkercompat.modifiers;

import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;
import slimeknights.tconstruct.library.modifiers.Modifier;

public class EpicFightModifiers {
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(EpicFightTinkerCompat.MODID);

    public static final StaticModifier<EpicFightPartStatModifier> EPIC_PART_STATS_CALCULATOR = MODIFIERS.register("epic_part_stats_calculator", EpicFightPartStatModifier::new);
}