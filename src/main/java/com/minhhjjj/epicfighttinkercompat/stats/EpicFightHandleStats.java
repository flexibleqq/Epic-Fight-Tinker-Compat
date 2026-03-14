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
import com.minhhjjj.epicfighttinkercompat.EpicFightToolStats;

import java.util.ArrayList;
import java.util.List;

import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltip;
import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltipKey;

/** Stats tùy chỉnh cho phần cán cầm (Epic Fight) */
// Record bây giờ chỉ chứa đúng 1 biến là maxStrikes
public record EpicFightHandleStats(float maxStrikes) implements IMaterialStats {
    
    // Đăng ký ID: "epicfighttinkercompat:handle"
    public static final MaterialStatsId ID = new MaterialStatsId(EpicFightTinkerCompat.MODID, "handle");
    
    // Bộ đọc từ file JSON: Chỉ tìm đúng dòng "max_strikes"
    public static final MaterialStatType<EpicFightHandleStats> TYPE = new MaterialStatType<>(
        ID, 
        new EpicFightHandleStats(0f), // Giá trị mặc định nếu JSON không ghi gì
        RecordLoadable.create(
            FloatLoadable.ANY.defaultField("max_strikes", 0f, true, EpicFightHandleStats::maxStrikes),
            EpicFightHandleStats::new
        )
    );

    // Prefix cho Tooltip (Bạn có thể tái sử dụng file en_us.json đã tạo)
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
        // formatColoredPercentBoost sẽ tự động đổi -0.1 thành -10% và tô màu
        list.add(IToolStat.formatColoredPercentBoost(MAX_STRIKES_PREFIX, this.maxStrikes));
        return list;
    }

    @Override
    public List<Component> getLocalizedDescriptions() {
        return DESCRIPTION;
    }

    @Override
    public void apply(ModifierStatsBuilder builder, float scale) {
        // Áp dụng chỉ số vào ToolBuilder
        // Tùy thuộc vào class Custom Stat (EpicFightMultiplierStat) của bạn dùng hàm gì
        // Thường dùng .add() vì class Custom của bạn đã tự xử lý nó thành multiplier
        EpicFightToolStats.MAX_STRIKES.percent(builder, this.maxStrikes * scale);
    }
}