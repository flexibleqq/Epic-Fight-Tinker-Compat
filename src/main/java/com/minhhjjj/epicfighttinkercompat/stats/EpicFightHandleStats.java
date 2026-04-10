package com.minhhjjj.epicfighttinkercompat.stats;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;

import java.util.ArrayList;
import java.util.List;

import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltip;
import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltipKey;

public record EpicFightHandleStats(float maxStrikes) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(EpicFightTinkerCompat.MODID, "handle");
    public static final MaterialStatType<EpicFightHandleStats> TYPE = new MaterialStatType<>(
        ID, 
        new EpicFightHandleStats(0f),
        RecordLoadable.create(
            FloatLoadable.ANY.defaultField("max_strikes", 0f, true, EpicFightHandleStats::maxStrikes),
            EpicFightHandleStats::new
        )
    );

    private static final String MAX_STRIKES_PREFIX = "stat.epicfighttinkercompat.max_strikes";
    private static final List<Component> DESCRIPTION = List.of(
        Component.translatable("stat.epicfighttinkercompat.max_strikes.description")
    );

    @Override
    public MaterialStatType<EpicFightHandleStats> getType() {
        return TYPE;
    }

    @Override
    public List<Component> getLocalizedInfo() {
        List<Component> list = new ArrayList<>();
        list.add(IToolStat.formatColoredPercentBoost(MAX_STRIKES_PREFIX, this.maxStrikes));
        return list;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        EpicFightToolStats.MAX_STRIKES.percent(builder, this.maxStrikes * scale);
    }
}