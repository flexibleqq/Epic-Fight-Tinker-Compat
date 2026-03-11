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

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EpicFightTinkerCompat.MODID)
public class EpicFightTinkerCompat
{
    public static final String MODID = "epicfighttinkercompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EpicFightTinkerCompat(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        EpicFightModifier.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
    }

    @SuppressWarnings("null")
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
        EpicFightToolStats.register();
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
