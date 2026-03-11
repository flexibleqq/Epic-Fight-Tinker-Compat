package com.minhhjjj.epicfighttinkercompat;

import com.mojang.datafixers.util.Pair;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
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
        if (tool != null && !tool.isBroken()) {
            MaterialNBT materials = tool.getMaterials();
            var statTypes = tool.getDefinition().getData().getHook(ToolHooks.TOOL_MATERIALS).getStatTypes(tool.getDefinition());
            var parts = tool.getDefinition().getData().getHook(ToolHooks.TOOL_PARTS).getParts(tool.getDefinition());

            int limit = Math.min(Math.min(materials.size(), statTypes.size()), parts.size());
            for (int i = 0; i < limit; i++) {
                slimeknights.tconstruct.library.materials.definition.MaterialVariantId matId = materials.get(i).getVariant();
                MaterialStatsId partType = statTypes.get(i);
                String partItemId = BuiltInRegistries.ITEM.getKey(parts.get(i).asItem()).toString();

                EpicFightMaterialStatReader.PartBonuses bonuses = EpicFightMaterialStatReader.read(matId.getId(), partType, partItemId);
                impactBonus += bonuses.impact();
                strikesBonus += bonuses.maxStrikes();
                armorNegationBonus += bonuses.armorNegation();
            }
        }

        if (weaponType.equals("cleaver")) {
            WeaponCapability.Builder builder = WeaponCapability.builder()
                .category(WeaponCategories.GREATSWORD)
                .styleProvider((entity) -> Styles.TWO_HAND)
                .hitParticle(EpicFightParticles.HIT_BLADE.get())
                .hitSound(EpicFightSounds.BLADE_HIT.get())
                .swingSound(EpicFightSounds.WHOOSH_BIG.get())
                .newStyleCombo(Styles.TWO_HAND, Animations.GREATSWORD_AUTO1, Animations.GREATSWORD_AUTO2, Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH)
                .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_GREATSWORD)
                .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_WALK_GREATSWORD)
                .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_RUN_GREATSWORD)
                .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.GREATSWORD_GUARD)
                .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_SNEAK)
                .collider(ColliderPreset.GREATSWORD);

            // LƯU Ý 3: Đưa chỉ số đã cộng dồn vào Capability (Màu trắng siêu đẹp)
            UUID modUUID = UUID.fromString("77777777-8888-9999-0000-111111111111");
            
            if (impactBonus != 0) {
                builder.addStyleAttibutes(Styles.TWO_HAND, Pair.of(
                    EpicFightAttributes.IMPACT.get(), 
                    new AttributeModifier(modUUID, "Tinker Impact", impactBonus, AttributeModifier.Operation.ADDITION)
                ));
            }
            if (strikesBonus != 0) {
                builder.addStyleAttibutes(Styles.TWO_HAND, Pair.of(
                    EpicFightAttributes.MAX_STRIKES.get(), 
                    new AttributeModifier(modUUID, "Tinker Strikes", strikesBonus, AttributeModifier.Operation.ADDITION)
                ));
            }
            if (armorNegationBonus != 0) {
                builder.addStyleAttibutes(Styles.TWO_HAND, Pair.of(
                    EpicFightAttributes.ARMOR_NEGATION.get(), 
                    new AttributeModifier(modUUID, "Tinker Armor Negation", armorNegationBonus, AttributeModifier.Operation.ADDITION)
                ));
            }

            return builder.build();
        }
        else if (weaponType.equals("javelin")) {
            WeaponCapability.Builder builder = WeaponCapability.builder()
                .category(WeaponCategories.TRIDENT)
                .styleProvider((entity) -> Styles.ONE_HAND)
                .hitParticle(EpicFightParticles.HIT_BLADE.get())
                .hitSound(EpicFightSounds.BLADE_HIT.get())
                .swingSound(EpicFightSounds.WHOOSH_BIG.get())
                .newStyleCombo(Styles.ONE_HAND, Animations.TRIDENT_AUTO1, Animations.TRIDENT_AUTO2, Animations.TRIDENT_AUTO3, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH)
                .livingMotionModifier(Styles.ONE_HAND, LivingMotions.AIM, Animations.BIPED_JAVELIN_AIM)
                .livingMotionModifier(Styles.ONE_HAND, LivingMotions.SHOT, Animations.BIPED_JAVELIN_THROW)
                .collider(ColliderPreset.SPEAR);

            // LƯU Ý 3: Đưa chỉ số đã cộng dồn vào Capability (Màu trắng siêu đẹp)
            UUID modUUID = UUID.fromString("77777777-8888-9999-0000-111111111111");
            
            if (impactBonus != 0) {
                builder.addStyleAttibutes(Styles.ONE_HAND, Pair.of(
                    EpicFightAttributes.IMPACT.get(), 
                    new AttributeModifier(modUUID, "Tinker Impact", impactBonus, AttributeModifier.Operation.ADDITION)
                ));
            }
            if (strikesBonus != 0) {
                builder.addStyleAttibutes(Styles.ONE_HAND, Pair.of(
                    EpicFightAttributes.MAX_STRIKES.get(), 
                    new AttributeModifier(modUUID, "Tinker Strikes", strikesBonus, AttributeModifier.Operation.ADDITION)
                ));
            }
            if (armorNegationBonus != 0) {
                builder.addStyleAttibutes(Styles.ONE_HAND, Pair.of(
                    EpicFightAttributes.ARMOR_NEGATION.get(), 
                    new AttributeModifier(modUUID, "Tinker Armor Negation", armorNegationBonus, AttributeModifier.Operation.ADDITION)
                ));
            }

            return builder.build();
        }

        return null;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == EpicFightCapabilities.CAPABILITY_ITEM) {
            return optionalCapability.cast();
        }
        return LazyOptional.empty();
    }
}