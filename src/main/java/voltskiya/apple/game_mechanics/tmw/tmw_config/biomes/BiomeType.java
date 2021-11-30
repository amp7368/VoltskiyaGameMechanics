package voltskiya.apple.game_mechanics.tmw.tmw_config.biomes;

import net.minecraft.resources.MinecraftKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.mobs.MobType;
import voltskiya.apple.utilities.util.minecraft.InventoryUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeType {
    private final BiomeTypeBuilder.BiomeIcon icon;
    private final HashMap<MobType, Integer> mobs;
    private final int spawnRate;
    private final HashMap<TemperatureTime, TemperatureInfo> dailyTemperatures;
    private final WindInfo windInfo;
    private int biomeUid;

    public BiomeType(BiomeTypeBuilder builder) {
        this.icon = builder.icon;
        this.spawnRate = builder.spawnRate;
        this.mobs = builder.mobs;
        this.dailyTemperatures = builder.dailyTemperatures;
        this.windInfo = builder.windInfo;
        this.biomeUid = builder.biomeUid <= 0 ? BiomeTypeDatabase.getCurrentBiomeUid() : builder.biomeUid;
    }

    public ItemStack toItem() {
        return icon.toItem();
    }

    public BiomeTypeBuilder toBuilder() {
        return new BiomeTypeBuilder(this);
    }

    public String getName() {
        return icon.getName();
    }

    @Override
    public int hashCode() {
        return biomeUid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BiomeType other) {
            return other.biomeUid == this.biomeUid;
        }
        return false;
    }

    @Override
    public String toString() {
        return icon == null ? null : icon.getName();
    }

    public int getTypicalTempNow(long time) {
        final TemperatureInfo temperatureInfo = dailyTemperatures.get(TemperatureTime.getTime(time));
        return temperatureInfo == null ? -100 : temperatureInfo.degrees; // null should be impossible
    }

    public double getWind() {
        return (windInfo.kphMax + windInfo.kphMin) / 2d;
    }

    public int getUid() {
        return biomeUid;
    }

    public void validateUid() {
        if (this.biomeUid <= 0) this.biomeUid = BiomeTypeDatabase.getCurrentBiomeUid();
    }

    public double getUsefulSpawnRate() {
        return spawnRate / 10d;
    }

    public Map<MobType, Integer> getSpawns() {
        return this.mobs;
    }

    public Map<MobType, Double> getSpawnPercentages() {
        double mobCount = 0;
        for (Integer mob : this.mobs.values()) {
            mobCount += mob;
        }
        Map<MobType, Double> percentages = new HashMap<>();
        for (Map.Entry<MobType, Integer> mob : this.mobs.entrySet()) {
            percentages.put(mob.getKey(), mob.getValue() / mobCount);
        }
        return percentages;
    }

    public enum TemperatureTime {
        MORNING,
        NOON,
        EVENING,
        MIDNIGHT;

        public static TemperatureTime getTime(long time) {
            time += 6000;
            time %= 24000;
            if (time < 3000) return MIDNIGHT;
            else if (time < 9000) return MORNING;
            else if (time < 16000) return NOON;
            else return EVENING;
        }
    }

    public static class BiomeTypeBuilder {
        private BiomeIcon icon;
        private int spawnRate = 0;
        public HashMap<MobType, Integer> mobs = new HashMap<>();
        private HashMap<TemperatureTime, TemperatureInfo> dailyTemperatures = new HashMap<>();
        private WindInfo windInfo;
        public int biomeUid = -1;

        public BiomeTypeBuilder(BiomeType real) {
            this.icon = real.icon;
            this.spawnRate = real.spawnRate;
            this.mobs = real.mobs;
            this.dailyTemperatures = real.dailyTemperatures;
            this.windInfo = real.windInfo;
            this.biomeUid = real.biomeUid;
            if (this.windInfo == null) this.windInfo = new WindInfo();
            if (this.dailyTemperatures == null) this.dailyTemperatures = new HashMap<>();
            for (TemperatureTime time : TemperatureTime.values())
                this.dailyTemperatures.putIfAbsent(time, new TemperatureInfo());

        }

        public BiomeTypeBuilder() {
            icon = null;
            this.windInfo = new WindInfo();
            for (TemperatureTime time : TemperatureTime.values())
                this.dailyTemperatures.putIfAbsent(time, new TemperatureInfo());
        }

        public void setIcon(BiomeIcon icon) {
            this.icon = icon;
        }

        public ItemStack getIconItem() {
            return icon == null ? InventoryUtils.makeItem(Material.LEVER, 1, "No spawn egg", null) : icon.toItem();
        }

        public BiomeType build() {
            return new BiomeType(this);
        }


        public int getSpawnRate() {
            return spawnRate;
        }


        @Override
        public int hashCode() {
            return icon.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof BiomeType && icon.name.equals(((BiomeType) obj).icon.name);
        }

        public HashMap<MobType, Integer> getMobs() {
            return mobs;
        }

        public Integer getMob(MobType mob) {
            return mobs.get(mob);
        }

        public void incrementMob(MobType mob, int change) {
            this.mobs.computeIfPresent(mob, (m, count) -> count + change);
        }

        public void addMob(MobType mob) {
            this.mobs.putIfAbsent(mob, 1);
        }

        public HashMap<TemperatureTime, TemperatureInfo> getDailyTemperatures() {
            return this.dailyTemperatures;
        }

        public WindInfo getWind() {
            return this.windInfo;
        }

        @Nullable
        public String getName() {
            return icon == null ? null : icon.getName();
        }

        public void incrementSpawnRate(int i) {
            this.spawnRate += i;
        }

        public void removeMob(MobType mob) {
            this.mobs.remove(mob);
        }

        public List<MinecraftKey> getMinecraftBiomes() {
            return BiomeTypeDatabase.get().getMinecraftBiomes(this);
        }

        public static class BiomeIcon {
            private final String name;
            private final Material material;
            private final List<String> lore;

            public BiomeIcon(String name, Material material, List<String> lore) {
                this.name = name;
                this.material = material;
                this.lore = lore;
            }

            public ItemStack toItem() {
                ItemStack item = new ItemStack(material);
                final ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(name);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                return item;
            }

            public String getName() {
                return name;
            }
        }
    }

    public static class TemperatureInfo {
        public int degrees = 0;
    }

    public static class WindInfo {
        public int kphMin = 0;
        public int kphMax = 0;
    }
}
