package com.minhhjjj.epicfighttinkercompat.modifiers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.mantle.client.TooltipKey;

import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;
import com.minhhjjj.epicfighttinkercompat.EpicFightToolStats;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightHeadStats;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightHandleStats;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightBindingStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightHelmetStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightChestplateStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightLeggingsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightBootsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightMailleStats;
import java.util.List;

public class EpicFightPartStatModifier extends Modifier implements ToolStatsModifierHook, TooltipModifierHook {

    @Override
    public int getPriority() {
        return 99999; 
    }

    @Override
    public boolean shouldDisplay(boolean advanced) {
        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        super.registerHooks(hookBuilder);
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
        hookBuilder.addHook(this, ModifierHooks.TOOLTIP); 
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {

        if (tool.getItem() instanceof net.minecraft.world.item.ArmorItem) {
            float weight = tool.getStats().get(EpicFightToolStats.WEIGHT);
            float stunArmor = tool.getStats().get(EpicFightToolStats.STUN_ARMOR);

            if (weight != EpicFightToolStats.WEIGHT.getDefaultValue()) {
                tooltip.add(EpicFightToolStats.WEIGHT.formatValue(weight));
            }
            if (stunArmor != EpicFightToolStats.STUN_ARMOR.getDefaultValue()) {
                tooltip.add(EpicFightToolStats.STUN_ARMOR.formatValue(stunArmor));
            }
        }
        else {
            float impact = tool.getStats().get(EpicFightToolStats.IMPACT);
            float maxStrikes = tool.getStats().get(EpicFightToolStats.MAX_STRIKES);
            float armorNegation = tool.getStats().get(EpicFightToolStats.ARMOR_NEGATION);

            if (player != null) {
                impact += player.getAttributeBaseValue(EpicFightAttributes.IMPACT.get());
                armorNegation += player.getAttributeBaseValue(EpicFightAttributes.ARMOR_NEGATION.get());
                maxStrikes += player.getAttributeBaseValue(EpicFightAttributes.MAX_STRIKES.get());
            }

            if (impact != EpicFightToolStats.IMPACT.getDefaultValue()) {
                tooltip.add(EpicFightToolStats.IMPACT.formatValue(impact));
            }
            if (maxStrikes != EpicFightToolStats.MAX_STRIKES.getDefaultValue()) {
                tooltip.add(EpicFightToolStats.MAX_STRIKES.formatValue(maxStrikes));
            }
            if (armorNegation != EpicFightToolStats.ARMOR_NEGATION.getDefaultValue()) {
                tooltip.add(EpicFightToolStats.ARMOR_NEGATION.formatValue(armorNegation));
            }
        }
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        List<MaterialVariant> materials = context.getMaterials().getList();
        List<MaterialStatsId> partRoles = ToolMaterialHook.stats(context.getDefinition());

        for (int i = 0; i < materials.size(); i++) {
            if (i >= partRoles.size()) break;
            if (!partRoles.get(i).getNamespace().equals("tconstruct")) continue;

            MaterialVariant material = materials.get(i);
            String id = partRoles.get(i).getPath();

            if (id.equals("head")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightHeadStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightHeadStats headStats) {
                            EpicFightToolStats.IMPACT.add(builder, headStats.impact());
                        }
                    });
            }
            else if (id.equals("handle")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightHandleStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightHandleStats handleStats) {
                            EpicFightToolStats.MAX_STRIKES.percent(builder, handleStats.maxStrikes()); 
                        }
                    });
            }
            else if (id.equals("binding")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightBindingStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightBindingStats bindingStats) {
                            EpicFightToolStats.ARMOR_NEGATION.add(builder, bindingStats.armorNegation());
                        }
                    });
            }
            else if (id.equals("plating_helmet")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightHelmetStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightHelmetStats helmetStats) {
                            EpicFightToolStats.WEIGHT.add(builder, helmetStats.weight());
                            EpicFightToolStats.STUN_ARMOR.add(builder, helmetStats.stunArmor());
                        }
                    });
            }
            else if (id.equals("plating_chestplate")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightChestplateStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightChestplateStats chestplateStats) {
                            EpicFightToolStats.WEIGHT.add(builder, chestplateStats.weight());
                            EpicFightToolStats.STUN_ARMOR.add(builder, chestplateStats.stunArmor());
                        }
                    });
            }
            else if (id.equals("plating_leggings")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightLeggingsStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightLeggingsStats leggingsStats) {
                            EpicFightToolStats.WEIGHT.add(builder, leggingsStats.weight());
                            EpicFightToolStats.STUN_ARMOR.add(builder, leggingsStats.stunArmor());
                        }
                    });
            }
            else if (id.equals("plating_boots")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightBootsStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightBootsStats bootsStats) {
                            EpicFightToolStats.WEIGHT.add(builder, bootsStats.weight());
                            EpicFightToolStats.STUN_ARMOR.add(builder, bootsStats.stunArmor());
                        }
                    });
            }
            else if (id.equals("maille")) {
                MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightMailleStats.ID)
                    .ifPresent(stats -> {
                        if (stats instanceof EpicFightMailleStats mailleStats) {
                            EpicFightToolStats.WEIGHT.percent(builder, mailleStats.weight()); 
                            EpicFightToolStats.STUN_ARMOR.percent(builder, mailleStats.stunArmor()); 
                        }
                    });
            }
        }
    }
}