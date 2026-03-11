package com.minhhjjj.epicfighttinkercompat.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;
import com.minhhjjj.epicfighttinkercompat.EpicFightMaterialStatReader;

@Mod.EventBusSubscriber(modid = EpicFightTinkerCompat.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipHandler {

    @SubscribeEvent
    public static void onTooltipRender(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (item instanceof IToolPart part) {
            boolean isShiftPressed = Screen.hasShiftDown();
            // Nếu không bấm Shift thì thoát luôn, không cần làm gì cả
            if (!isShiftPressed) return;

            MaterialVariantId material = part.getMaterial(stack);
            MaterialStatsId partType = part.getStatType();
            String partItemId = BuiltInRegistries.ITEM.getKey(part.asItem()).toString();
            List<Component> tooltip = event.getToolTip();

            // 1. TÌM VỊ TRÍ ĐỂ CHÈN
            int insertIndex = tooltip.size(); // Mặc định ở cuối
            boolean foundAnchor = false;

            boolean isBindingPart = isBindingPart(partType, partItemId);
            if (isBindingPart) {
                // Với binding: ưu tiên vị trí dòng "No stats" (nếu có), vì đó là điểm neo chính xác nhất.
                String bindingHeader = Component.translatable("stat.tconstruct.binding").getString().toLowerCase(Locale.ROOT);
                String noStatsLine = Component.translatable("tool_stat.tconstruct.extra.no_stats").getString().toLowerCase(Locale.ROOT);

                int noStatsIndex = -1;
                for (int i = 0; i < tooltip.size(); i++) {
                    String lineText = tooltip.get(i).getString().toLowerCase(Locale.ROOT);
                    if (!noStatsLine.equals("tool_stat.tconstruct.extra.no_stats") && lineText.contains(noStatsLine)) {
                        noStatsIndex = i;
                        break;
                    }
                }

                if (noStatsIndex >= 0) {
                    // Thay trực tiếp dòng "No stats" bằng chỉ số custom.
                    tooltip.remove(noStatsIndex);
                    insertIndex = noStatsIndex;
                    foundAnchor = true;
                } else {
                    int bindingHeaderIndex = -1;
                    for (int i = 0; i < tooltip.size(); i++) {
                        String lineText = tooltip.get(i).getString().toLowerCase(Locale.ROOT);
                        if (!bindingHeader.equals("stat.tconstruct.binding") && lineText.contains(bindingHeader)) {
                            bindingHeaderIndex = i;
                            break;
                        }
                    }
                    if (bindingHeaderIndex >= 0) {
                        insertIndex = bindingHeaderIndex + 1;
                        foundAnchor = true;
                    }
                }
            }

            if (!foundAnchor) {
                // Quét từng dòng từ trên xuống dưới
                for (int i = 0; i < tooltip.size(); i++) {
                    // Lấy nội dung chữ của dòng đó và chuyển thành chữ thường để dễ so sánh
                    String lineText = tooltip.get(i).getString().toLowerCase(Locale.ROOT);

                    // Nếu phát hiện dòng này chứa chữ Sát thương (Tiếng Anh hoặc Tiếng Việt)
                    if (lineText.contains("melee damage") || lineText.contains("attack damage") || lineText.contains("sát thương")) {
                        insertIndex = i + 1; // Lấy đúng vị trí NGAY BÊN DƯỚI dòng đó
                        foundAnchor = true;
                        break;
                    }
                }
            }

            // Nếu bộ phận đó KHÔNG CÓ dòng sát thương (ví dụ như Phụ kiện - Binding)
            // Thì chúng ta mới quay lại cách cũ: Tìm dòng trống đầu tiên
            if (!foundAnchor) {
                for (int i = 1; i < tooltip.size(); i++) {
                    if (tooltip.get(i).getString().trim().isEmpty()) {
                        insertIndex = i;
                        break;
                    }
                }
            }

            // 2. GOM CÁC CHỈ SỐ CẦN CHÈN
            List<Component> statsToInsert = new ArrayList<>();
                EpicFightMaterialStatReader.PartBonuses bonuses = EpicFightMaterialStatReader.read(material.getId(), partType, partItemId);

            if (bonuses.impact() != 0) {
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.impact")
                        .append(Component.literal(": " + bonuses.impact())));
            }
            if (bonuses.maxStrikes() != 0) {
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.max_strikes")
                        .append(Component.literal(": " + bonuses.maxStrikes()*100 + "%")));
            }
            if (bonuses.armorNegation() != 0) {
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.armor_negation")
                        .append(Component.literal(": " + bonuses.armorNegation()*100 + "%")));
            }

            // 3. THỰC HIỆN CHÈN VÀO ĐÚNG VỊ TRÍ
            if (!statsToInsert.isEmpty()) {
                // Lệnh này sẽ đẩy các chữ bên dưới xuống để nhường chỗ cho chỉ số của chúng ta
                tooltip.addAll(insertIndex, statsToInsert);
            }
        }
    }

    private static boolean isBindingPart(MaterialStatsId partType, String partItemId) {
        String statTypeId = partType.toString();
        return statTypeId.endsWith(":binding")
                || statTypeId.endsWith(":extra")
                || partItemId.endsWith(":tool_binding")
                || partItemId.endsWith(":tough_binding");
    }
}