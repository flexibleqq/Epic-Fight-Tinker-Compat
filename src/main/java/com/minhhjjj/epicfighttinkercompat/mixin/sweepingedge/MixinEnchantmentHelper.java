package com.minhhjjj.epicfighttinkercompat.mixin.sweepingedge;

import java.util.LinkedHashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "getTagEnchantmentLevel", at = @At("RETURN"), cancellable = true, remap = false)
    private static void getTinkersSweepingLevel(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment == Enchantments.SWEEPING_EDGE && stack.getItem() instanceof IModifiable) {
            
            if (cir.getReturnValue() == 0) {
                ToolStack tool = ToolStack.from(stack);
                ModifierId sweepingModifier = new ModifierId("tconstruct", "sweeping_edge");
                int tinkersSweepingLevel = tool.getModifiers().getLevel(sweepingModifier);
                
                if (tinkersSweepingLevel > 0) {
                    cir.setReturnValue(tinkersSweepingLevel);
                }
            }
        }
    }

    @Inject(method = "getEnchantments", at = @At("RETURN"), cancellable = true)
    private static void addTinkersEnchantmentsToMap(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir) {
        if (stack.getItem() instanceof IModifiable) {
            ToolStack tool = ToolStack.from(stack);
            ModifierId sweepingModifier = new ModifierId("tconstruct", "sweeping_edge");
            int tinkersSweepingLevel = tool.getModifiers().getLevel(sweepingModifier);

            if (tinkersSweepingLevel > 0) {
                Map<Enchantment, Integer> currentEnchants = cir.getReturnValue();
                
                if (currentEnchants.getOrDefault(Enchantments.SWEEPING_EDGE, 0) < tinkersSweepingLevel) {
                    Map<Enchantment, Integer> newEnchants = new LinkedHashMap<>(currentEnchants);
                    newEnchants.put(Enchantments.SWEEPING_EDGE, tinkersSweepingLevel);
                    cir.setReturnValue(newEnchants);
                }
            }
        }
    }
}