package voltskiya.apple.game_mechanics.tmw.tmw_config.mobs;

import apple.mc.utilities.data.serialize.EntitySerializable;
import apple.mc.utilities.inventory.item.InventoryUtils;
import apple.utilities.json.gson.serialize.JsonSerializing;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import de.tr7zw.nbtapi.NBTCompound;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import voltskiya.apple.game_mechanics.tmw.tmw_config.biomes.gui.BiomeTypeBuilderRegisterBlocks;

public class MobType {

    private static final Random random = new Random();
    private final MobTypeBuilder.MobIcon icon;
    private final boolean isPersistent;
    private final double despawnsAfterHours;
    private final boolean isSpawnWithLineOfSight;
    private final int highestYLevel;
    private final int lowestYLevel;
    private final TimeToSpawn timeToSpawn;
    private final ArrayList<Integer> groups;

    public MobType(MobTypeBuilder builder) {
        this.icon = builder.icon;
        this.isPersistent = builder.isPersistent;
        this.despawnsAfterHours = builder.despawnsAfterHours;
        this.isSpawnWithLineOfSight = builder.isSpawnWithLineOfSight;
        this.highestYLevel = builder.highestYLevel;
        this.lowestYLevel = builder.lowestYLevel;
        this.timeToSpawn = builder.timeToSpawn;
        this.groups = builder.groups;
    }

    public ItemStack toItem() {
        return icon.toItem();
    }

    public MobTypeBuilder toBuilder() {
        return new MobTypeBuilder(this);
    }

    public String getName() {
        return icon == null ? "" : icon.getName();
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

    public boolean isPersistent() {
        return isPersistent;
    }

    public double getDespawnsAfterHours() {
        return despawnsAfterHours;
    }

    public boolean isSpawnWithLineOfSight() {
        return isSpawnWithLineOfSight;
    }

    public int getHighestYLevel() {
        return highestYLevel;
    }

    public int getLowestYLevel() {
        return lowestYLevel;
    }

    public TimeToSpawn getTimeToSpawn() {
        return timeToSpawn;
    }

    public double getMeanGroup() {
        int total = 0;
        for (int group : groups) {
            total += group;
        }

        return total / (double) groups.size();
    }

    public boolean canSpawn(BiomeTypeBuilderRegisterBlocks.TopBlock topBlock) {
        //todo
        return true;
    }

    public int getGroup() {
        if (groups.isEmpty()) {
            return 0;
        }
        return groups.get(random.nextInt(groups.size()));
    }

    public long getDespawnAt() {
        return (long) (System.currentTimeMillis() + this.despawnsAfterHours * 60 * 1000);
    }

    public NBTCompound getEnitityNbt() {
        return this.icon.getNbt().getEntityTag();
    }

    @Nullable
    public Entity spawn(Location location) {
        return this.icon.getNbt().spawn(location);
    }

    public static class MobTypeSerializer implements JsonSerializing<MobType> {

        private static final MobTypeSerializer instance = new MobTypeSerializer();

        public static MobTypeSerializer get() {
            return instance;
        }

        @Override
        public JsonElement serialize(MobType mobType, Type type,
            JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(mobType.icon.getName());
        }

        @Override
        public MobType deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return MobTypeDatabase.getMob(jsonElement.getAsString());
        }
    }

    public static class MobTypeBuilder {

        public ArrayList<Integer> groups = new ArrayList<>() {{
            add(1);
        }};
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
            this.groups = real.groups;
        }

        public MobTypeBuilder() {
            icon = null;
        }

        public void setIcon(MobIcon icon) {
            this.icon = icon;
        }

        public ItemStack getIconItem() {
            return icon == null ? InventoryUtils.get()
                .makeItem(Material.LEVER, 1, "No spawn egg", null) : icon.toItem();
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
            if (timeToSpawn == null) {
                timeToSpawn = new TimeToSpawn();
            }
            return timeToSpawn;
        }

        public void addGroup() {
            this.groups.add(1);
        }

        public void groupIncrement(int index, int amount) {
            if (index < this.groups.size()) {
                this.groups.set(index, this.groups.get(index) + amount);
            }
        }

        public boolean isDone() {
            return !icon.name.isBlank();
        }

        public String getName() {
            return icon == null ? "" : icon.getName();
        }

        public static class MobIcon {

            private final String name;
            private final Material material;
            private final List<String> lore;
            private final EntitySerializable nbt;

            public MobIcon(ItemStack item) {
                InventoryUtils utils = InventoryUtils.get();
                this.name = utils.getDisplayName(item);
                this.material = item.getType();
                this.lore = utils.getLore(item);
                this.nbt = new EntitySerializable(item);
            }

            public ItemStack toItem() {
                return InventoryUtils.get().makeItem(material, 1, name, lore);
            }

            public EntitySerializable getNbt() {
                return nbt;
            }

            public String getName() {
                return name;
            }
        }
    }

    public static class TimeToSpawn {

        private boolean isDay = true;
        private boolean isEvening = true;
        private boolean isNight = true;
        private boolean isMorning = true;

        public boolean isDay() {
            return isDay;
        }

        public void toggleDay() {
            isDay = !isDay;
        }

        public boolean isEvening() {
            return isEvening;
        }

        public void toggleEvening() {
            isEvening = !isEvening;
        }

        public boolean isNight() {
            return isNight;
        }

        public void toggleNight() {
            isNight = !isNight;
        }

        public boolean isMorning() {
            return isMorning;
        }

        public void toggleMorning() {
            isMorning = !isMorning;
        }
    }
}
