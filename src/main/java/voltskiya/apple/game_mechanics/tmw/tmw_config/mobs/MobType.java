package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs;

import com.google.gson.*;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import voltskiya.apple.game_mechanics.util.minecraft.InventoryUtils;

import java.lang.reflect.Type;
import java.util.List;

public class MobType {
    private final MobTypeBuilder.MobIcon icon;
    private final boolean isPersistent;
    private final double despawnsAfterHours;
    private final boolean isSpawnWithLineOfSight;
    private final int highestYLevel;
    private final int lowestYLevel;
    private final TimeToSpawn timeToSpawn;

    public MobType(MobTypeBuilder builder) {
        this.icon = builder.icon;
        this.isPersistent = builder.isPersistent;
        this.despawnsAfterHours = builder.despawnsAfterHours;
        this.isSpawnWithLineOfSight = builder.isSpawnWithLineOfSight;
        this.highestYLevel = builder.highestYLevel;
        this.lowestYLevel = builder.lowestYLevel;
        this.timeToSpawn = builder.timeToSpawn;
    }

    public ItemStack toItem() {
        return icon.toItem();
    }

    public MobTypeBuilder toBuilder() {
        return new MobTypeBuilder(this);
    }

    public String getName() {
        return icon.getName();
    }

    @Override
    public int hashCode() {
        return this.icon.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MobType && this.icon.getName().equals(((MobType) obj).getName());
    }

    @Override
    public String toString() {
        return this.icon.getName();
    }

    public static class MobTypeSerializer implements JsonSerializer<MobType> {
        @Override
        public JsonElement serialize(MobType mobType, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(mobType.icon.getName());
        }
    }

    public static class MobTypeDeSerializer implements JsonDeserializer<MobType> {
        @Override
        public MobType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return MobTypeDatabase.getMob(jsonElement.getAsString());
        }
    }

    public static class MobTypeBuilder {
        private MobIcon icon;
        private boolean isPersistent = false;
        private double despawnsAfterHours = 0;
        private boolean isSpawnWithLineOfSight = false;
        private int highestYLevel = 255;
        private int lowestYLevel = 0;
        private TimeToSpawn timeToSpawn = new TimeToSpawn();

        public MobTypeBuilder(MobType real) {
            this.icon = real.icon;
            this.isPersistent = real.isPersistent;
            this.despawnsAfterHours = real.despawnsAfterHours;
            this.isSpawnWithLineOfSight = real.isSpawnWithLineOfSight;
            this.highestYLevel = real.highestYLevel;
            this.lowestYLevel = real.lowestYLevel;
            this.timeToSpawn = real.timeToSpawn;
        }

        public MobTypeBuilder() {
            icon = null;
        }

        public void setIcon(MobIcon icon) {
            this.icon = icon;
        }

        public ItemStack getIconItem() {
            return icon == null ? InventoryUtils.makeItem(Material.LEVER, 1, "No spawn egg", null) : icon.toItem();
        }

        public MobType build() {
            return new MobType(this);
        }

        public boolean isPersistent() {
            return isPersistent;
        }

        public void togglePersistent() {
            isPersistent = !isPersistent;
        }

        public double getDespawnsAfterHours() {
            return despawnsAfterHours;
        }

        public void changeDespawnsAfterHours(double change) {
            this.despawnsAfterHours += change;
        }

        public boolean isSpawnWithLineOfSight() {
            return isSpawnWithLineOfSight;
        }

        public void toggleSpawnWithLineOfSight() {
            isSpawnWithLineOfSight = !isSpawnWithLineOfSight;
        }

        public int getHighestYLevel() {
            return highestYLevel;
        }

        public void changeHighestYLevel(int change) {
            this.highestYLevel += change;
            this.highestYLevel = Math.max(0, this.highestYLevel);
            this.highestYLevel = Math.min(255, this.highestYLevel);
        }

        public int getLowestYLevel() {
            return lowestYLevel;
        }

        public void changeLowestYLevel(int change) {
            this.lowestYLevel += change;
            this.lowestYLevel = Math.max(0, this.lowestYLevel);
            this.lowestYLevel = Math.min(255, this.lowestYLevel);
        }

        public TimeToSpawn getTimeToSpawn() {
            return timeToSpawn;
        }

        public void setTimeToSpawn(TimeToSpawn timeToSpawn) {
            this.timeToSpawn = timeToSpawn;
        }

        public static class MobIcon {
            private final String name;
            private final Material material;
            private final List<String> lore;
            private final NBTTagCompound nbt;

            public MobIcon(String name, Material material, List<String> lore, NBTTagCompound nbt, EntityTypes<?> entityTypes) {
                this.name = name;
                this.material = material;
                this.lore = lore;
                this.nbt = nbt;
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

    private static class TimeToSpawn {
    }
}
