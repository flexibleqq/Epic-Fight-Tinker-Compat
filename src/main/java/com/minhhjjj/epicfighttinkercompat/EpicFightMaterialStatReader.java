package com.minhhjjj.epicfighttinkercompat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.server.ServerLifecycleHooks;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class EpicFightMaterialStatReader {
  private EpicFightMaterialStatReader() {}

  public record PartBonuses(float impact, float maxStrikes, float armorNegation, float weight, float stunArmor) {
    public static final PartBonuses ZERO = new PartBonuses(0f, 0f, 0f, 0f, 0f);
  }

  private static final Map<String, JsonObject> MATERIAL_JSON_CACHE = new ConcurrentHashMap<>();
  private static final JsonObject NO_STATS = new JsonObject();
  private static volatile boolean preloaded = false;

  public static synchronized void preload(ResourceManager resourceManager) {
    MATERIAL_JSON_CACHE.clear();
    preloaded = false;

    Collection<ResourceLocation> files = resourceManager.listResources(
      "tinkering/materials/stats",
      location -> location.getPath().endsWith(".json")
    ).keySet();

    for (ResourceLocation file : files) {
      ResourceLocation materialKey = materialKeyFromFile(file);
      if (materialKey == null) {
        continue;
      }
      loadStatsObject(resourceManager, materialKey);
    }
    preloaded = true;
  }

  public static PartBonuses read(MaterialId materialId, MaterialStatsId partType) {
    ensureCacheReady();
    return readCached(materialId, partType, null);
  }

  public static PartBonuses read(ResourceManager resourceManager, MaterialId materialId, MaterialStatsId partType) {
    if (!preloaded) {
      preload(resourceManager);
    }
    return readCached(materialId, partType, null);
  }

  public static PartBonuses read(MaterialId materialId, MaterialStatsId partType, String partItemId) {
    ensureCacheReady();
    return readCached(materialId, partType, partItemId);
  }

  private static void ensureCacheReady() {
    if (preloaded) {
      return;
    }
    ResourceManager resourceManager = resolveResourceManager();
    if (resourceManager == null) {
      return;
    }
    preload(resourceManager);
  }

  private static PartBonuses readCached(MaterialId materialId, MaterialStatsId partType, String partItemId) {
    ResourceLocation materialKey = ResourceLocation.tryParse(materialId.toString());
    if (materialKey == null) {
      return PartBonuses.ZERO;
    }

    JsonObject statsObject = MATERIAL_JSON_CACHE.get(materialKey.toString());
    if (statsObject == NO_STATS) {
      return PartBonuses.ZERO;
    }
    if (statsObject == null) {
      return PartBonuses.ZERO;
    }

    String partTypeId = partType.toString();
    String itemId = partItemId == null ? "" : partItemId;

    if (itemId.endsWith(":small_blade") || itemId.endsWith(":broad_blade") || itemId.endsWith(":large_plate")) {
      float impact = getFloat(statsObject, EpicFightStatDefinitions.JSON_HEAD_KEY, "impact");
      return new PartBonuses(impact, 0f, 0f, 0f, 0f);
    }
    if (itemId.endsWith(":tool_handle") || itemId.endsWith(":tough_handle")) {
      float maxStrikes = getFloat(statsObject, EpicFightStatDefinitions.JSON_HANDLE_KEY, "max_strikes");
      return new PartBonuses(0f, maxStrikes, 0f, 0f, 0f);
    }
    if (itemId.endsWith(":tool_binding") || itemId.endsWith(":tough_binding") || itemId.endsWith(":bowstring") || itemId.endsWith(":fletching") || itemId.endsWith(":arrow_shaft") || itemId.endsWith(":arrow_head") || itemId.endsWith(":shield_core") || itemId.endsWith(":maille")) {
      float armorNegation = getFloat(statsObject, EpicFightStatDefinitions.JSON_BINDING_KEY, "armor_negation");
      return new PartBonuses(0f, 0f, armorNegation, 0f, 0f);
    }

    if (itemId.endsWith(":helmet_plating")) {
      float weight = getFloat(statsObject, EpicFightStatDefinitions.JSON_HELMET_KEY, "weight");
      float stunArmor = getFloat(statsObject, EpicFightStatDefinitions.JSON_HELMET_KEY, "stun_armor");
      return new PartBonuses(0f, 0f, 0f, weight, stunArmor);
    }

    if (partTypeId.endsWith(":head")) {
      float impact = getFloat(statsObject, EpicFightStatDefinitions.JSON_HEAD_KEY, "impact");
      return new PartBonuses(impact, 0f, 0f, 0f, 0f);
    }
    if (partTypeId.endsWith(":handle") || partTypeId.endsWith(":grip")) {
      float maxStrikes = getFloat(statsObject, EpicFightStatDefinitions.JSON_HANDLE_KEY, "max_strikes");
      return new PartBonuses(0f, maxStrikes, 0f, 0f, 0f);
    }
    if (partTypeId.endsWith(":binding") || partTypeId.endsWith(":extra")) {
      float armorNegation = getFloat(statsObject, EpicFightStatDefinitions.JSON_BINDING_KEY, "armor_negation");
      return new PartBonuses(0f, 0f, armorNegation, 0f, 0f);
    }
    return PartBonuses.ZERO;
  }

  private static JsonObject loadStatsObject(ResourceManager resourceManager, ResourceLocation materialKey) {
    String cacheKey = materialKey.toString();
    if (MATERIAL_JSON_CACHE.containsKey(cacheKey)) {
      JsonObject cached = MATERIAL_JSON_CACHE.get(cacheKey);
      return cached == NO_STATS ? null : cached;
    }

    ResourceLocation file = ResourceLocation.fromNamespaceAndPath(
      materialKey.getNamespace(),
      "tinkering/materials/stats/" + materialKey.getPath() + ".json"
    );

    Optional<Resource> resourceOpt = resourceManager.getResource(file);
    if (resourceOpt.isEmpty()) {
      MATERIAL_JSON_CACHE.put(cacheKey, NO_STATS);
      return null;
    }

    try (InputStreamReader reader = new InputStreamReader(resourceOpt.get().open(), StandardCharsets.UTF_8)) {
      JsonElement root = JsonParser.parseReader(reader);
      if (!root.isJsonObject()) {
        MATERIAL_JSON_CACHE.put(cacheKey, NO_STATS);
        return null;
      }
      JsonObject rootObj = root.getAsJsonObject();
      if (!rootObj.has("stats") || !rootObj.get("stats").isJsonObject()) {
        MATERIAL_JSON_CACHE.put(cacheKey, NO_STATS);
        return null;
      }
      JsonObject statsObj = rootObj.getAsJsonObject("stats");
      MATERIAL_JSON_CACHE.put(cacheKey, statsObj);
      return statsObj;
    } catch (Exception ignored) {
      MATERIAL_JSON_CACHE.put(cacheKey, NO_STATS);
      return null;
    }
  }

  private static ResourceLocation materialKeyFromFile(ResourceLocation file) {
    String path = file.getPath();
    String prefix = "tinkering/materials/stats/";
    if (!path.startsWith(prefix) || !path.endsWith(".json")) {
      return null;
    }
    String materialPath = path.substring(prefix.length(), path.length() - ".json".length());
    return ResourceLocation.fromNamespaceAndPath(file.getNamespace(), materialPath);
  }

  private static float getFloat(JsonObject statsObject, String key, String field) {
    if (!statsObject.has(key) || !statsObject.get(key).isJsonObject()) {
      return 0f;
    }
    JsonObject part = statsObject.getAsJsonObject(key);
    if (!part.has(field) || !part.get(field).isJsonPrimitive()) {
      return 0f;
    }
    try {
      return part.get(field).getAsFloat();
    } catch (Exception ignored) {
      return 0f;
    }
  }

  private static ResourceManager resolveResourceManager() {
    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    if (server != null) {
      return server.getResourceManager();
    }

    try {
      Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
      Object minecraft = minecraftClass.getMethod("getInstance").invoke(null);
      Object manager = minecraftClass.getMethod("getResourceManager").invoke(minecraft);
      return (ResourceManager) manager;
    } catch (Throwable ignored) {
      return null;
    }
  }
}
