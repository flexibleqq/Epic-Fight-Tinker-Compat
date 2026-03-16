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

public record EpicFightBindingStats(float armorNegation) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(EpicFightTinkerCompat.MODID, "binding");
    public static final MaterialStatType<EpicFightBindingStats> TYPE = new MaterialStatType<>(
        ID, 
        new EpicFightBindingStats(0f),
        RecordLoadable.create(
            FloatLoadable.ANY.defaultField("armor_negation", 0f, true, EpicFightBindingStats::armorNegation),
            EpicFightBindingStats::new
        )
    );

    private static final List<Component> DESCRIPTION = List.of(
        Component.translatable("stat.epicfighttinkercompat.armor_negation.description")
    );

    @Override
    public MaterialStatType<EpicFightBindingStats> getType() {
        return TYPE;
    }

    @Override
    public List<Component> getLocalizedInfo() {
        List<Component> info = new ArrayList<>();
        info.add(EpicFightToolStats.ARMOR_NEGATION.formatValue(this.armorNegation));
        return info;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        EpicFightToolStats.ARMOR_NEGATION.add(builder, this.armorNegation * scale);
    }
}