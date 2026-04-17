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
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightHeadStats;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightHandleStats;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightBindingStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightHelmetStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightChestplateStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightLeggingsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightBootsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightMailleStats;
import com.minhhjjj.epicfighttinkercompat.tool.EpicFightArmorStatsHelper;

@Mod.EventBusSubscriber(modid = EpicFightTinkerCompat.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipHandler {

    private enum PartCategory {
        NONE,
        MELEE,
        ARMOR
    }

    @SubscribeEvent
    public static void onTooltipRender(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (EpicFightArmorStatsHelper.isModifiableArmor(item)) {
            ToolStack tool = ToolStack.from(stack);
            if (!EpicFightArmorStatsHelper.hasEpicFightPartStats(tool)) {
                EpicFightArmorStatsHelper.ArmorStats armorStats = EpicFightArmorStatsHelper.resolveArmorStats(tool);
                if (armorStats.weight() != 0.0D || armorStats.stunArmor() != 0.0D) {
                    List<Component> tooltip = event.getToolTip();
                    tooltip.add(Component.translatable("stat.epicfighttinkercompat.weight")
                        .append(Component.literal(formatValue(armorStats.weight())).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x7A40D6)))));
                    tooltip.add(Component.translatable("stat.epicfighttinkercompat.stun_armor")
                        .append(Component.literal(formatValue(armorStats.stunArmor())).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5E40D6)))));
                }
            }
        }

        if (item instanceof IToolPart part) {
            boolean isShiftPressed = Screen.hasShiftDown();
            if (!isShiftPressed) return;

            MaterialVariantId material = part.getMaterial(stack);
            MaterialStatsId partType = part.getStatType();
            String partItemId = BuiltInRegistries.ITEM.getKey(part.asItem()).toString();
            List<Component> tooltip = event.getToolTip();

            int insertIndex = tooltip.size();
            boolean foundAnchor = false;
            PartCategory partCategory = PartCategory.NONE;

            // boolean isNoStats = isNoStats(partType, partItemId);
            // if (isNoStats) {
                // else {
                //     int bindingHeaderIndex = -1;
                //     for (int i = 0; i < tooltip.size(); i++) {
                //         String lineText = tooltip.get(i).getString().toLowerCase(Locale.ROOT);
                //         if (!bindingHeader.equals("stat.tconstruct.binding") && lineText.contains(bindingHeader)) {
                //             bindingHeaderIndex = i;
                //             break;
                //         }
                //     }
                //     if (bindingHeaderIndex >= 0) {
                //         insertIndex = bindingHeaderIndex + 1;
                //         foundAnchor = true;
                //     }
                // }
            // }


            float impact = 0;
            float maxStrikes = 0;
            float armorNegation = 0;
            float weight = 0;
            float stunArmor = 0;

            String typePath = partType.getPath();

            if (typePath.equals("head")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightHeadStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightHeadStats hStats) {
                    impact = hStats.impact();
                    partCategory = impact!=0?PartCategory.MELEE:PartCategory.NONE;
                }
            } 
            else if (typePath.equals("handle")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightHandleStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightHandleStats hStats) {
                    maxStrikes = hStats.maxStrikes();
                    partCategory = maxStrikes!=0?PartCategory.MELEE:PartCategory.NONE;
                }
            } 
            else if (typePath.equals("binding") || typePath.equals("extra")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightBindingStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightBindingStats bStats) {
                    armorNegation = bStats.armorNegation();
                    partCategory = armorNegation!=0?PartCategory.MELEE:PartCategory.NONE;
                }
            } 
            else if (typePath.equals("plating_helmet")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightHelmetStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightHelmetStats hStats) {
                    weight = hStats.weight();
                    stunArmor = hStats.stunArmor();
                    partCategory = PartCategory.ARMOR;
                }
            } 
            else if (typePath.equals("plating_chestplate")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightChestplateStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightChestplateStats cStats) {
                    weight = cStats.weight();
                    stunArmor = cStats.stunArmor();
                    partCategory = PartCategory.ARMOR;
                }
            } 
            else if (typePath.equals("plating_leggings")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightLeggingsStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightLeggingsStats lStats) {
                    weight = lStats.weight();
                    stunArmor = lStats.stunArmor();
                    partCategory = PartCategory.ARMOR;
                }
            } 
            else if (typePath.equals("plating_boots")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightBootsStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightBootsStats bStats) {
                    weight = bStats.weight();
                    stunArmor = bStats.stunArmor();
                    partCategory = PartCategory.ARMOR;
                }
            }
            else if (typePath.equals("maille")) {
                Optional<IMaterialStats> opt = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightMailleStats.ID);
                if (opt.isPresent() && opt.get() instanceof EpicFightMailleStats mStats) {
                    weight = mStats.weight();
                    stunArmor = mStats.stunArmor();
                    partCategory = PartCategory.ARMOR;
                }
            }
            if (partCategory == PartCategory.NONE) return;

            boolean isNoStats = false;
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
                isNoStats = true;
                foundAnchor = true;
            }

            if (!foundAnchor) {
                for (int i = 0; i < tooltip.size(); i++) {
                    String lineText = tooltip.get(i).getString().toLowerCase(Locale.ROOT);

                    if (lineText.contains("melee damage") || lineText.contains("attack damage") || lineText.contains("knockback resist")) {
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

            if (partCategory == PartCategory.MELEE) {
                if (impact != 0) {
                    statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.impact")
                        .append(Component.literal("" + impact).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF5555)))));
                }

                if (maxStrikes != 0) {
                    statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.max_strikes")
                        .append(Component.literal((maxStrikes < 0 ? "" : "+") + (int)(maxStrikes * 100) + "%").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x55FF55)))));
                }

                if (armorNegation != 0) {
                    statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.armor_negation")
                        .append(Component.literal((int)armorNegation + "%").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5555FF)))));
                }
            }

            if (partCategory == PartCategory.ARMOR) {
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.weight")
                    .append(Component.literal(isNoStats?((weight<0?"":"+") + (int)(weight*100) + "%"):(weight%1==0?"" + (int)weight:String.format(Locale.US, "%.1f",weight))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x7A40D6)))));
                statsToInsert.add(Component.translatable("stat.epicfighttinkercompat.stun_armor")
                    .append(Component.literal(isNoStats?((weight<0?"":"+") + (int)(stunArmor*100) + "%"):(stunArmor%1==0?"" + (int)stunArmor:String.format(Locale.US, "%.1f",stunArmor))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x5E40D6)))));
            }

            if (!statsToInsert.isEmpty()) {
                tooltip.addAll(insertIndex, statsToInsert);
            }
        }
    }

    private static String formatValue(double value) {
        return value % 1 == 0 ? Integer.toString((int) value) : String.format(Locale.US, "%.1f", value);
    }

    // private static boolean isNoStats(MaterialStatsId partType, String partItemId) {
    //     String statTypeId = partType.toString();
    //     return statTypeId.endsWith(":binding")
    //             || statTypeId.endsWith(":maille")
    //             || partItemId.endsWith(":tool_binding")
    //             || partItemId.endsWith(":tough_binding");
    // }
}
