package com.minhhjjj.epicfighttinkercompat;

import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class EpicFightToolStats {

    // Khởi tạo trực tiếp không qua Forge DeferredRegister
    public static final FloatToolStat IMPACT = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "impact"), 0xFF5555, 0f, -100f, 100f);
        
    public static final FloatToolStat MAX_STRIKES = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "max_strikes"), 0x55FF55, 0f, -100f, 100f);
        
    public static final FloatToolStat ARMOR_NEGATION = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "armor_negation"), 0x5555FF, 0f, -100f, 100f);

    // Đăng ký trực tiếp vào hệ thống của Tinkers
    public static void register() {
        ToolStats.register(IMPACT);
        ToolStats.register(MAX_STRIKES);
        ToolStats.register(ARMOR_NEGATION);
    }
}