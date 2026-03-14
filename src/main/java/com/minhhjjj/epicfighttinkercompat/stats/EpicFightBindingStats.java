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

/** Stats tùy chỉnh cho phần Phụ kiện/Chốt nối (Binding) - Tập trung vào Armor Negation */
public record EpicFightBindingStats(float armorNegation) implements IMaterialStats {
    
    // Đăng ký ID: "epicfighttinkercompat:binding"
    public static final MaterialStatsId ID = new MaterialStatsId(EpicFightTinkerCompat.MODID, "binding");
    
    // Bộ đọc từ file JSON: Quét dòng "armor_negation"
    public static final MaterialStatType<EpicFightBindingStats> TYPE = new MaterialStatType<>(
        ID, 
        new EpicFightBindingStats(0f), // Mặc định là 0 nếu JSON không ghi gì
        RecordLoadable.create(
            FloatLoadable.ANY.defaultField("armor_negation", 0f, true, EpicFightBindingStats::armorNegation),
            EpicFightBindingStats::new
        )
    );

    // Lấy mô tả từ file en_us.json
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
        // Dùng formatValue của ARMOR_NEGATION để nó tự tô màu xanh dương (0x5555FF)
        info.add(EpicFightToolStats.ARMOR_NEGATION.formatValue(this.armorNegation));
        return info;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        // Cộng thẳng điểm Xuyên giáp vào vũ khí.
        // Phụ kiện thường có scale nhỏ hơn (ví dụ 0.5) so với lưỡi (1.0).
        EpicFightToolStats.ARMOR_NEGATION.add(builder, this.armorNegation * scale);
    }
}