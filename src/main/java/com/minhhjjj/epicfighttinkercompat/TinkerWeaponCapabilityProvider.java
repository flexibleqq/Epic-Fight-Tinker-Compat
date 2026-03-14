package com.minhhjjj.epicfighttinkercompat;

import com.mojang.datafixers.util.Pair;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCapabilityPresets;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.server.commands.arguments.SkillArgument;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.UUID;

public class TinkerWeaponCapabilityProvider implements ICapabilityProvider {
    private final LazyOptional<CapabilityItem> optionalCapability;
    public static final ResourceLocation EPIC_CAP_ID = ResourceLocation.fromNamespaceAndPath(EpicFightTinkerCompat.MODID, "weapon_cap");

    // LƯU Ý 1: Constructor giờ nhận thêm ToolStack để đọc dữ liệu
    public TinkerWeaponCapabilityProvider(String weaponType, ToolStack tool) {
        CapabilityItem eCapabilityItem = createCapabilityItem(weaponType, tool);

        if (eCapabilityItem == null) {
            this.optionalCapability = LazyOptional.empty();
        } else {
            this.optionalCapability = LazyOptional.of(() -> eCapabilityItem);
        }
    }

    private CapabilityItem createCapabilityItem(String weaponType, ToolStack tool) {
        // Lấy trực tiếp stat đã hoàn chỉnh từ tool (bao gồm cộng dồn từ hook TOOL_STATS)
        double impactBonus = 0.0;
        double strikesBonus = 0.0;
        double armorNegationBonus = 0.0;

        if (tool != null) {
            impactBonus = tool.getStats().get(EpicFightToolStats.IMPACT);
            strikesBonus = tool.getStats().get(EpicFightToolStats.MAX_STRIKES);
            armorNegationBonus = tool.getStats().get(EpicFightToolStats.ARMOR_NEGATION);
        }

        // Cộng bonus từ từng material theo phần (vd: manyullyn head → impact)
        // if (tool != null && !tool.isBroken()) {
        //     MaterialNBT materials = tool.getMaterials();
        //     var statTypes = tool.getDefinition().getData().getHook(ToolHooks.TOOL_MATERIALS).getStatTypes(tool.getDefinition());
        //     var parts = tool.getDefinition().getData().getHook(ToolHooks.TOOL_PARTS).getParts(tool.getDefinition());

        //     int limit = Math.min(Math.min(materials.size(), statTypes.size()), parts.size());
        //     for (int i = 0; i < limit; i++) {
        //         slimeknights.tconstruct.library.materials.definition.MaterialVariantId matId = materials.get(i).getVariant();
        //         MaterialStatsId partType = statTypes.get(i);
        //         String partItemId = BuiltInRegistries.ITEM.getKey(parts.get(i).asItem()).toString();

        //         EpicFightMaterialStatReader.PartBonuses bonuses = EpicFightMaterialStatReader.read(matId.getId(), partType, partItemId);
        //         impactBonus += bonuses.impact();
        //         strikesBonus += bonuses.maxStrikes();
        //         armorNegationBonus += bonuses.armorNegation();
        //     }
        // }

        CapabilityItem.Builder builder = null;
        if (weaponType.equals("cleaver")) {
            builder = WeaponCapabilityPresets.GREATSWORD.apply(tool.getItem());
        }
        else if (weaponType.equals("javelin")) {
            builder = WeaponCapabilityPresets.TRIDENT.apply(tool.getItem());
        }
        else if (weaponType.equals("sword") || weaponType.equals("battlesign")) {
            builder = WeaponCapabilityPresets.SWORD.apply(tool.getItem());
        }
        else if (weaponType.equals("sledge_hammer") || weaponType.equals("vein_hammer")) {
            builder = WeaponCapabilityPresets.GREATSWORD.apply(tool.getItem());
        }
        else if (weaponType.equals("axe") || weaponType.equals("broad_axe")) {
            builder = WeaponCapabilityPresets.AXE.apply(tool.getItem());
        }
        else if (weaponType.equals("scythe")) {
            builder = WeaponCapabilityPresets.GREATSWORD.apply(tool.getItem());
        }
        else if (weaponType.equals("dagger")) {
            builder = WeaponCapabilityPresets.DAGGER.apply(tool.getItem());
        }
        else if (weaponType.equals("sky_staff") || weaponType.equals("earth_staff") || weaponType.equals("ender_staff") || weaponType.equals("ichor_staff")) {
            builder = WeaponCapabilityPresets.TRIDENT.apply(tool.getItem());
        }
        else if (weaponType.equals("longbow")) {
            builder = WeaponCapabilityPresets.BOW.apply(tool.getItem());
        }
        else if (weaponType.equals("crossbow")) {
            builder = WeaponCapabilityPresets.CROSSBOW.apply(tool.getItem());
        }
        else if (weaponType.equals("pickaxe") || weaponType.equals("pickadze") || weaponType.equals("war_pick")) {
            builder = WeaponCapabilityPresets.PICKAXE.apply(tool.getItem());
        }
        else if (weaponType.equals("shovel")) {
            builder = WeaponCapabilityPresets.SHOVEL.apply(tool.getItem());
        }
        else if (weaponType.equals("hoe") || weaponType.equals("kama") || weaponType.equals("mattock")) {
            builder = WeaponCapabilityPresets.HOE.apply(tool.getItem());
        }
        else if (weaponType.equals("melting_pan")) {
            builder = WeaponCapabilityPresets.TRIDENT.apply(tool.getItem());
        }
        else if (weaponType.equals("excavator")) {
            builder = WeaponCapabilityPresets.SHOVEL.apply(tool.getItem());
        }
        else if (weaponType.equals("throwing_axe")) {
            builder = WeaponCapabilityPresets.TRIDENT.apply(tool.getItem());
        }
        else if (weaponType.equals("plate_shield") || weaponType.equals("travelers_shield")) {
            builder = WeaponCapabilityPresets.SHIELD.apply(tool.getItem());
        }
        else if (weaponType.equals("swasher")) {
            builder = WeaponCapabilityPresets.FIST.apply(tool.getItem());
        }

        if (builder == null) {
            return null;
        }

        UUID modUUID = UUID.fromString("77777777-8888-9999-0000-111111111111");
        
        if (impactBonus != 0) {
            builder.addStyleAttibutes(Styles.COMMON, Pair.of(
                EpicFightAttributes.IMPACT.get(), 
                new AttributeModifier(modUUID, "Tinker Impact", impactBonus, AttributeModifier.Operation.ADDITION)
            ));
        }
        if (strikesBonus != 0) {
            builder.addStyleAttibutes(Styles.COMMON, Pair.of(
                EpicFightAttributes.MAX_STRIKES.get(), 
                new AttributeModifier(modUUID, "Tinker Strikes", strikesBonus, AttributeModifier.Operation.ADDITION)
            ));
        }
        if (armorNegationBonus != 0) {
            builder.addStyleAttibutes(Styles.COMMON, Pair.of(
                EpicFightAttributes.ARMOR_NEGATION.get(), 
                new AttributeModifier(modUUID, "Tinker Armor Negation", armorNegationBonus, AttributeModifier.Operation.ADDITION)
            ));
        }

        return builder.build();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == EpicFightCapabilities.CAPABILITY_ITEM) {
            return optionalCapability.cast();
        }
        return LazyOptional.empty();
    }
}