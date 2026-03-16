package com.minhhjjj.epicfighttinkercompat.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem;

import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;

@Mod.EventBusSubscriber(modid = EpicFightTinkerCompat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientFovHandler {

    @SubscribeEvent
    public static void onFovModify(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getUseItem();

        if (player.isUsingItem() && itemStack.getItem() instanceof ModifiableBowItem) {
            ToolStack tool = ToolStack.from(itemStack);
            float drawSpeed = tool.getStats().get(ToolStats.DRAW_SPEED); 
            
            if (drawSpeed <= 0.0f) {
                drawSpeed = 1.0f;
            }
            
            float maxDrawTicks = (1.0f / drawSpeed) * 20.0f; 

            int ticksInUse = itemStack.getUseDuration() - player.getUseItemRemainingTicks();
            float progress = (float) ticksInUse / maxDrawTicks;

            if (progress > 1.0F) {
                progress = 1.0F;
            } else {
                progress = progress * progress;
            }

            float fovModifier = 1.0F - (progress * 0.15F);
            event.setNewFovModifier(event.getFovModifier() * fovModifier);
        }
    }
}