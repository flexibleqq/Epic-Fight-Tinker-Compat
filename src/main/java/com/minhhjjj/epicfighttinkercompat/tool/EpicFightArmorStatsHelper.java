package com.minhhjjj.epicfighttinkercompat.tool;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;
import com.minhhjjj.epicfighttinkercompat.modifiers.EpicFightModifiers;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightToolStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightBootsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightChestplateStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightHelmetStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightLeggingsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightMailleStats;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;
import java.util.Optional;

public final class EpicFightArmorStatsHelper {
    private static final ModifierId EPIC_PART_STATS = new ModifierId(EpicFightTinkerCompat.MODID, "epic_part_stats_calculator");
    private static final MaterialStatsId TCON_HELMET = new MaterialStatsId("tconstruct", "plating_helmet");
    private static final MaterialStatsId TCON_CHESTPLATE = new MaterialStatsId("tconstruct", "plating_chestplate");
    private static final MaterialStatsId TCON_LEGGINGS = new MaterialStatsId("tconstruct", "plating_leggings");
    private static final MaterialStatsId TCON_BOOTS = new MaterialStatsId("tconstruct", "plating_boots");
    private static final MaterialStatsId TCON_MAILLE = new MaterialStatsId("tconstruct", "maille");

    private EpicFightArmorStatsHelper() {}

    public static boolean isModifiableArmor(Item item) {
        return item instanceof ArmorItem && item instanceof IModifiable;
    }

    public static boolean hasEpicFightPartStats(ToolStack tool) {
        return tool.getModifiers().getLevel(EPIC_PART_STATS) > 0;
    }

    public static ArmorStats resolveArmorStats(ToolStack tool) {
        double weight = tool.getStats().get(EpicFightToolStats.WEIGHT);
        double stunArmor = tool.getStats().get(EpicFightToolStats.STUN_ARMOR);

        if (!(tool.getItem() instanceof ArmorItem)) {
            return new ArmorStats(weight, stunArmor);
        }

        boolean hasEpicPartStats = hasEpicFightPartStats(tool);
        if (!hasEpicPartStats) {
            weight += getBaseArmorWeight(tool);
            stunArmor += getBaseArmorStunArmor(tool);
        }

        List<MaterialVariant> materials = tool.getMaterials().getList();
        List<MaterialStatsId> partRoles = ToolMaterialHook.stats(tool.getDefinition());

        for (int i = 0; i < materials.size() && i < partRoles.size(); i++) {
            MaterialVariant material = materials.get(i);
            String partRole = partRoles.get(i).getPath();

            switch (partRole) {
                case "plating_helmet" -> {
                    ArmorStats stats = getPlatingStats(material, EpicFightHelmetStats.ID, TCON_HELMET, SlotProfile.HELMET);
                    if (!hasEpicPartStats || !hasCustomArmorStats(material, EpicFightHelmetStats.ID)) {
                        weight += stats.weight();
                        stunArmor += stats.stunArmor();
                    }
                }
                case "plating_chestplate" -> {
                    ArmorStats stats = getPlatingStats(material, EpicFightChestplateStats.ID, TCON_CHESTPLATE, SlotProfile.CHESTPLATE);
                    if (!hasEpicPartStats || !hasCustomArmorStats(material, EpicFightChestplateStats.ID)) {
                        weight += stats.weight();
                        stunArmor += stats.stunArmor();
                    }
                }
                case "plating_leggings" -> {
                    ArmorStats stats = getPlatingStats(material, EpicFightLeggingsStats.ID, TCON_LEGGINGS, SlotProfile.LEGGINGS);
                    if (!hasEpicPartStats || !hasCustomArmorStats(material, EpicFightLeggingsStats.ID)) {
                        weight += stats.weight();
                        stunArmor += stats.stunArmor();
                    }
                }
                case "plating_boots" -> {
                    ArmorStats stats = getPlatingStats(material, EpicFightBootsStats.ID, TCON_BOOTS, SlotProfile.BOOTS);
                    if (!hasEpicPartStats || !hasCustomArmorStats(material, EpicFightBootsStats.ID)) {
                        weight += stats.weight();
                        stunArmor += stats.stunArmor();
                    }
                }
                case "maille" -> {
                    ArmorStats mailleStats = getMailleStats(material);
                    float mailleWeight = (float) mailleStats.weight();
                    float mailleStunArmor = (float) mailleStats.stunArmor();
                    weight *= (1.0D + mailleWeight);
                    stunArmor *= (1.0D + mailleStunArmor);
                }
                default -> {
                }
            }
        }

        return new ArmorStats(weight, stunArmor);
    }

    private static boolean hasCustomArmorStats(MaterialVariant material, MaterialStatsId statsId) {
        return MaterialRegistry.getInstance().getMaterialStats(material.getId(), statsId).isPresent();
    }

    private static ArmorStats getPlatingStats(MaterialVariant material, MaterialStatsId customId, MaterialStatsId nativeId, SlotProfile profile) {
        Optional<IMaterialStats> customStats = MaterialRegistry.getInstance().getMaterialStats(material.getId(), customId);
        if (customStats.isPresent()) {
            IMaterialStats stats = customStats.get();
            return new ArmorStats(readFloat(stats, "weight"), readFloat(stats, "stunArmor"));
        }

        Optional<IMaterialStats> nativeStats = MaterialRegistry.getInstance().getMaterialStats(material.getId(), nativeId);
        if (nativeStats.isEmpty()) {
            return new ArmorStats(0.0D, 0.0D);
        }

        IMaterialStats stats = nativeStats.get();
        double armor = readFloat(stats, "armor");
        double toughness = readFloat(stats, "toughness");
        double knockbackResistance = readFloat(stats, "knockbackResistance");

        double weight = Math.max(profile.minWeight, armor * profile.weightPerArmor)
            + (toughness * profile.weightPerToughness)
            + (knockbackResistance * profile.weightPerKnockbackResistance);
        double stunArmor = Math.max(profile.minStunArmor, armor * profile.stunPerArmor)
            + (toughness * profile.stunPerToughness)
            + (knockbackResistance * profile.stunPerKnockbackResistance);

        return new ArmorStats(round(weight), round(stunArmor));
    }

    private static ArmorStats getMailleStats(MaterialVariant material) {
        Optional<IMaterialStats> customStats = MaterialRegistry.getInstance().getMaterialStats(material.getId(), EpicFightMailleStats.ID);
        if (customStats.isPresent()) {
            IMaterialStats stats = customStats.get();
            return new ArmorStats(readFloat(stats, "weight"), readFloat(stats, "stunArmor"));
        }

        Optional<IMaterialStats> nativeStats = MaterialRegistry.getInstance().getMaterialStats(material.getId(), TCON_MAILLE);
        if (nativeStats.isEmpty()) {
            return new ArmorStats(0.0D, 0.0D);
        }

        IMaterialStats stats = nativeStats.get();
        double armor = readFloat(stats, "armor");
        double toughness = readFloat(stats, "toughness");
        double knockbackResistance = readFloat(stats, "knockbackResistance");

        double weight = (toughness * 0.05D) + (knockbackResistance * 0.5D) - (armor * 0.01D);
        double stunArmor = (toughness * 0.05D) + (knockbackResistance * 0.5D);
        return new ArmorStats(round(weight), round(stunArmor));
    }

    private static float readFloat(IMaterialStats stats, String methodName) {
        try {
            Object value = stats.getClass().getMethod(methodName).invoke(stats);
            if (value instanceof Number number) {
                return number.floatValue();
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return 0.0F;
    }

    private static double round(double value) {
        return Math.round(value * 100.0D) / 100.0D;
    }

    private static double getBaseArmorWeight(ToolStack tool) {
        String path = getItemPath(tool);
        if (path.startsWith("travelers_")) {
            return 0.75D;
        }
        return 0.0D;
    }

    private static double getBaseArmorStunArmor(ToolStack tool) {
        String path = getItemPath(tool);
        if (path.startsWith("travelers_")) {
            return 0.75D;
        }
        return 0.0D;
    }

    private static String getItemPath(ToolStack tool) {
        ResourceLocation itemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(tool.getItem());
        return itemId != null ? itemId.getPath() : "";
    }

    public record ArmorStats(double weight, double stunArmor) {}

    private record SlotProfile(
        double weightPerArmor,
        double minWeight,
        double weightPerToughness,
        double weightPerKnockbackResistance,
        double stunPerArmor,
        double minStunArmor,
        double stunPerToughness,
        double stunPerKnockbackResistance
    ) {
        private static final SlotProfile HELMET = new SlotProfile(3.0D, 6.0D, 2.0D, 6.0D, 0.5D, 0.5D, 0.5D, 5.0D);
        private static final SlotProfile CHESTPLATE = new SlotProfile(3.0D, 18.0D, 2.0D, 10.0D, 0.5D, 2.5D, 0.75D, 10.0D);
        private static final SlotProfile LEGGINGS = new SlotProfile(3.75D, 15.0D, 1.5D, 8.0D, 0.5D, 2.0D, 0.5D, 8.0D);
        private static final SlotProfile BOOTS = new SlotProfile(3.0D, 6.0D, 1.5D, 6.0D, 0.5D, 0.5D, 0.5D, 5.0D);
    }
}
