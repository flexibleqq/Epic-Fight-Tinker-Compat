package com.minhhjjj.epicfighttinkercompat;

import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class EpicFightToolStats {

    public static final FloatToolStat IMPACT = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "impact"), 0xFF5555, 0f, -1024f, 1024f, TinkerTags.Items.MODIFIABLE);
    public static final FloatToolStat MAX_STRIKES = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "max_strikes"), 0x55FF55, 0f, -1024f, 1024f, TinkerTags.Items.MODIFIABLE);
    public static final FloatToolStat ARMOR_NEGATION = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "armor_negation"), 0x5555FF, 0f, -1024f, 1024f, TinkerTags.Items.MODIFIABLE);

    public static final FloatToolStat WEIGHT = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "weight"), 0x7A40D6, 0f, -1024f, 1024f, TinkerTags.Items.WORN_ARMOR);
    public static final FloatToolStat STUN_ARMOR = new FloatToolStat(new ToolStatId(EpicFightTinkerCompat.MODID, "stun_armor"), 0x5E40D6, 0f, -1024f, 1024f, TinkerTags.Items.WORN_ARMOR);

    public static void register() {
        ToolStats.register(IMPACT);
        ToolStats.register(MAX_STRIKES);
        ToolStats.register(ARMOR_NEGATION);
        ToolStats.register(WEIGHT);
        ToolStats.register(STUN_ARMOR);
    }
}