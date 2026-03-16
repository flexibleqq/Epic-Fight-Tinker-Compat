package com.minhhjjj.epicfighttinkercompat;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.library.materials.MaterialRegistry;

import com.minhhjjj.epicfighttinkercompat.stats.EpicFightBindingStats;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightHandleStats;
import com.minhhjjj.epicfighttinkercompat.stats.EpicFightHeadStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightHelmetStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightChestplateStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightLeggingsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightBootsStats;
import com.minhhjjj.epicfighttinkercompat.stats.armor.EpicFightMailleStats;
import com.minhhjjj.epicfighttinkercompat.modifiers.EpicFightModifiers;
import com.minhhjjj.epicfighttinkercompat.skill.AutoGuardPassiveSkill;

import org.slf4j.Logger;

@Mod(EpicFightTinkerCompat.MODID)
public class EpicFightTinkerCompat
{
    public static final String MODID = "epicfighttinkercompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EpicFightTinkerCompat(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        EpicFightModifiers.MODIFIERS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    @SuppressWarnings("null")
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
        EpicFightToolStats.register();
        MaterialRegistry.getInstance().registerStatType(EpicFightHandleStats.TYPE);
        MaterialRegistry.getInstance().registerStatType(EpicFightHeadStats.TYPE);
        MaterialRegistry.getInstance().registerStatType(EpicFightBindingStats.TYPE);
        MaterialRegistry.getInstance().registerStatType(EpicFightHelmetStats.TYPE);
        MaterialRegistry.getInstance().registerStatType(EpicFightChestplateStats.TYPE);
        MaterialRegistry.getInstance().registerStatType(EpicFightLeggingsStats.TYPE);
        MaterialRegistry.getInstance().registerStatType(EpicFightBootsStats.TYPE);
        MaterialRegistry.getInstance().registerStatType(EpicFightMailleStats.TYPE);

    });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        EpicFightMaterialStatReader.preload(event.getServer().getResourceManager());
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(new EpicFightCacheReloadListener());
    }

    private static class EpicFightCacheReloadListener extends SimplePreparableReloadListener<Void> {
        @Override
        protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            return null;
        }

        @Override
        protected void apply(Void unused, ResourceManager resourceManager, ProfilerFiller profiler) {
            EpicFightMaterialStatReader.preload(resourceManager);
            LOGGER.info("Reloaded Epic Fight material stat cache");
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> EpicFightMaterialStatReader.preload(Minecraft.getInstance().getResourceManager()));
        }

        @SubscribeEvent
        public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event)
        {
            event.registerReloadListener(new EpicFightCacheReloadListener());
        }
    }
}
