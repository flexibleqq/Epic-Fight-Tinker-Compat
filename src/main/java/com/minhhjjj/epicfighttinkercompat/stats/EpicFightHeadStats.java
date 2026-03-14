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

/** Stats tùy chỉnh cho phần Lưỡi vũ khí (Epic Fight) - Tập trung vào Impact */
public record EpicFightHeadStats(float impact) implements IMaterialStats {
    
    // Đăng ký ID: "epicfighttinkercompat:head"
    public static final MaterialStatsId ID = new MaterialStatsId(EpicFightTinkerCompat.MODID, "head");
    
    // Bộ đọc từ file JSON: Bây giờ nó sẽ quét dòng "impact"
    public static final MaterialStatType<EpicFightHeadStats> TYPE = new MaterialStatType<>(
        ID, 
        new EpicFightHeadStats(0f), // Mặc định là 0 nếu JSON không có
        RecordLoadable.create(
            FloatLoadable.ANY.defaultField("impact", 0f, true, EpicFightHeadStats::impact),
            EpicFightHeadStats::new
        )
    );

    // Lấy mô tả từ file en_us.json của bạn
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
        // Dùng formatValue của IMPACT để nó tự tô màu đỏ (0xFF5555) bạn đã set sẵn
        info.add(EpicFightToolStats.IMPACT.formatValue(this.impact));
        return info;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        // Cộng thẳng điểm Impact vào vũ khí. 
        // Phép CỘNG (.add) rất phù hợp với phần Lưỡi (Head) vì nó cấu thành chỉ số gốc.
        EpicFightToolStats.IMPACT.add(builder, this.impact * scale);
    }
}