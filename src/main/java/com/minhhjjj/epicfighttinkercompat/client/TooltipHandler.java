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
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

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
            if (!isShiftPressed) return;

            MaterialVariantId material = part.getMaterial(stack);
            MaterialStatsId partType = part.getStatType();
            String partItemId = BuiltInRegistries.ITEM.getKey(part.asItem()).toString();
            List<Component> tooltip = event.getToolTip();

            int insertIndex = tooltip.size();
            boolean foundAnchor = false;

            boolean isBindingPart = isBindingPart(partType, partItemId);
            if (isBindingPart) {
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
                for (int i = 0; i < tooltip.size(); i++) {
                    String lineText = tooltip.get(i).getString().toLowerCase(Locale.ROOT);

                    if (lineText.contains("melee damage") || lineText.contains("attack damage") || lineText.contains("sát thương")) {
                        insertIndex = i + 1;
                        foundAnchor = true;
                        break;
                    }
                }
            }

            if (!foundAnchor) {
                for (int i = 1; i < tooltip.size(); i++) {
                    if (tooltip.get(i).getString().trim().isEmpty()) {
                        insertIndex = i;
                        break;
                    }
                }
            }

            List<Component> statsToInsert = new ArrayList<>();
                EpicFightMaterialStatReader.PartBonuses bonuses = EpicFightMaterialStatReader.read(material.getId(), partType, partItemId);

            if (bonuses.impact() != 0) {
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.impact")
                    .append(Component.literal((bonuses.impact()<0)?"":"+"+bonuses.impact()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF5555)))));
            }
            if (bonuses.maxStrikes() != 0) {
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.max_strikes")
                    .append(Component.literal((bonuses.maxStrikes()<0)?"":"+" + (int)(bonuses.maxStrikes()*100) + "%").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55FF55)))));
            }
            if (bonuses.armorNegation() != 0) {
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.armor_negation")
                    .append(Component.literal((bonuses.armorNegation()<0)?"":"+"+(int)bonuses.armorNegation() + "%")
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5555FF)))));
            }

            if (!statsToInsert.isEmpty()) {
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