package com.minhhjjj.epicfighttinkercompat.stats;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;
import com.minhhjjj.epicfighttinkercompat.EpicFightToolStats;

import java.util.ArrayList;
import java.util.List;

public record EpicFightHeadStats(float impact) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(EpicFightTinkerCompat.MODID, "head");
    public static final MaterialStatType<EpicFightHeadStats> TYPE = new MaterialStatType<>(
        ID, 
        new EpicFightHeadStats(0f),
        RecordLoadable.create(
            FloatLoadable.ANY.defaultField("impact", 0f, true, EpicFightHeadStats::impact),
            EpicFightHeadStats::new
        )
    );

    private static final List<Component> DESCRIPTION = List.of(
        Component.translatable("stat.epicfighttinkercompat.impact.description")
    );

    @Override
    public MaterialStatType<EpicFightHeadStats> getType() {
        return TYPE;
    }

    @Override
    public List<Component> getLocalizedInfo() {
        List<Component> info = new ArrayList<>();
        info.add(EpicFightToolStats.IMPACT.formatValue(this.impact));
        return info;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        EpicFightToolStats.IMPACT.add(builder, this.impact * scale);
    }
}