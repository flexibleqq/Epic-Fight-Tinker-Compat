package com.minhhjjj.epicfighttinkercompat.client;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.PathPackResources;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModFileInfo;
import java.nio.file.Path;
import com.minhhjjj.epicfighttinkercompat.EpicFightTinkerCompat;

@Mod.EventBusSubscriber(modid = EpicFightTinkerCompat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModResourcePackHandler {

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            
            IModFileInfo modFileInfo = ModList.get().getModFileById(EpicFightTinkerCompat.MODID);
            if (modFileInfo == null) return;

            Path resourcePath = modFileInfo.getFile().findResource("resourcepacks", "tinker_size_enhance");

            Pack pack = Pack.readMetaAndCreate(
                    "builtin/tcon_text_fix",
                    Component.literal("TConstruct Text Fix Pack"),
                    true,
                    (name) -> new PathPackResources(name, resourcePath, true),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP, 
                    PackSource.BUILT_IN
            );

            if (pack != null) {
                event.addRepositorySource((source) -> source.accept(pack));
            }
        }
    }
}