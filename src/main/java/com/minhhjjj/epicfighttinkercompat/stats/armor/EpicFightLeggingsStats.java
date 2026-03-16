package com.minhhjjj.epicfighttinkercompat.stats.armor;

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

public record EpicFightLeggingsStats(float weight, float stunArmor) implements IMaterialStats {
    public static final MaterialStatsId ID = new MaterialStatsId(EpicFightTinkerCompat.MODID, "plating_leggings");
    public static final MaterialStatType<EpicFightLeggingsStats> TYPE = new MaterialStatType<>(
        ID, 
        new EpicFightLeggingsStats(0f, 0f),
        RecordLoadable.create(
            FloatLoadable.ANY.defaultField("weight", 0f, true, EpicFightLeggingsStats::weight),
            FloatLoadable.ANY.defaultField("stun_armor", 0f, true, EpicFightLeggingsStats::stunArmor),
            EpicFightLeggingsStats::new
        )
    );

    private static final List<Component> DESCRIPTION = List.of(
        Component.translatable("stat.epicfighttinkercompat.weight.description"),
        Component.translatable("stat.epicfighttinkercompat.stun_armor.description")
    );

    @Override
    public MaterialStatType<EpicFightLeggingsStats> getType() {
        return TYPE;
    }

    @Override
    public List<Component> getLocalizedInfo() {
        List<Component> info = new ArrayList<>();
        info.add(EpicFightToolStats.WEIGHT.formatValue(this.weight));
        info.add(EpicFightToolStats.STUN_ARMOR.formatValue(this.stunArmor));
        return info;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        EpicFightToolStats.WEIGHT.add(builder, this.weight * scale);
        EpicFightToolStats.STUN_ARMOR.add(builder, this.stunArmor * scale);
    }
}