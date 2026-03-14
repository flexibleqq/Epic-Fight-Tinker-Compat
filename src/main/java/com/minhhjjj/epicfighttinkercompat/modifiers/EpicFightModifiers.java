package com.minhhjjj.epicfighttinkercompat.modifiers;

import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;

public class EpicFightModifiers {
    
    // Dùng ModifierDeferredRegister của riêng Tinkers thay vì DeferredRegister của Forge
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(EpicFightTinkerCompat.MODID);

    // Đăng ký Modifier ẩn của bạn
    public static final StaticModifier<EpicFightPartStatModifier> EPIC_PART_STATS_CALCULATOR = MODIFIERS.register("epic_part_stats_calculator", EpicFightPartStatModifier::new);
}