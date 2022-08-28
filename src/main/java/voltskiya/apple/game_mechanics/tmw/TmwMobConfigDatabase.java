package voltskiya.apple.game_mechanics.tmw;

import apple.utilities.database.SaveFileable;
import apple.utilities.util.FileFormatting;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class TmwMobConfigDatabase implements SaveFileable {

    private static TmwMobConfigDatabase instance;
    public final Map<UUID, MobConfigPerWorld> worldSpawning = new HashMap<>();
    public long regenInterval = 200;
    public boolean isSpawningMobs = false;

    public TmwMobConfigDatabase() {
        instance = this;
    }

    public static TmwMobConfigDatabase get() {
        return instance;
    }

    @NotNull
    public static String getFileNameExtStatic() {
        return FileFormatting.extensionJson(getFileNameStatic());
    }

    public static String getFileNameStatic() {
        return "TmwMobConfig";
    }

    @Override
    public String getSaveFileName() {
        return getFileNameExtStatic();
    }

    private void save() {
        PluginTMW.get().saveTmwMobConfig();
    }

    public synchronized long getRegenInterval() {
        return regenInterval;
    }

    public synchronized void setRegenInterval(int regenInterval) {
        this.regenInterval = regenInterval;
        save();
    }

    public synchronized boolean isSpawningMobs() {
        return isSpawningMobs;
    }

    public synchronized void setIsSpawningMobs(boolean isSpawningMobs) {
        this.isSpawningMobs = isSpawningMobs;
        save();
    }

    public synchronized void setWorldSpawning(UUID worldUUID, boolean isSpawningMobs) {
        this.worldSpawning.computeIfAbsent(worldUUID, (k) -> new MobConfigPerWorld())
            .setMobSpawning(isSpawningMobs);
        save();
    }

    public synchronized boolean getWorldSpawning(UUID worldUUID) {
        return this.worldSpawning.computeIfAbsent(worldUUID, (k) -> new MobConfigPerWorld())
            .isMobSpawning();
    }

    public Map<UUID, MobConfigPerWorld> getAllWorlds() {
        return new HashMap<>(this.worldSpawning);
    }
}
