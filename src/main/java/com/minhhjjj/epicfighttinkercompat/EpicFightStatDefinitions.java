package com.minhhjjj.epicfighttinkercompat;

import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

public final class EpicFightStatDefinitions {
    private EpicFightStatDefinitions() {}

    // Standard TConstruct part stat IDs used by tool parts
    public static final MaterialStatsId PART_HEAD = new MaterialStatsId("tconstruct", "head");
    public static final MaterialStatsId PART_HANDLE = new MaterialStatsId("tconstruct", "handle");
    public static final MaterialStatsId PART_BINDING = new MaterialStatsId("tconstruct", "binding");

    // JSON keys under data/<namespace>/tinkering/materials/stats/<material>.json -> stats {}
    public static final String JSON_HEAD_KEY = EpicFightTinkerCompat.MODID + ":head";
    public static final String JSON_HANDLE_KEY = EpicFightTinkerCompat.MODID + ":handle";
    public static final String JSON_BINDING_KEY = EpicFightTinkerCompat.MODID + ":binding";
}