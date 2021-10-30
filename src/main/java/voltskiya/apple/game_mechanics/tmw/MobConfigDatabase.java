package voltskiya.apple.game_mechanics.tmw;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.game_mechanics.util.FileIOService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobConfigDatabase implements SaveFileable {
    private static final AppleJsonDatabaseSingleton<MobConfigDatabase> databaseManager = new AppleJsonDatabaseSingleton<>(
            PluginTMW.get().getFile(PluginTMW.MOBS_FOLDER),
            FileIOService.get()
    );
    private static MobConfigDatabase instance;
    private final HashMap<UUID, MobConfigPerWorld> worldSpawning = new HashMap<>();
    private int regenInterval = 60;
    private boolean isSpawningMobs = true;

    public static void loadNow() {
        instance = databaseManager.loadNow(MobConfigDatabase.class, getFileNameStatic());
        if (instance == null) {
            instance = new MobConfigDatabase();
            instance.save();
        }
    }

    public static MobConfigDatabase get() {
        return instance;
    }

    @NotNull
    private static String getFileNameStatic() {
        return "mobsConfig.json";
    }

    @Override
    public String getSaveFileName() {
        return getFileNameStatic();
    }

    private void save() {
        databaseManager.save(this);
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
        this.worldSpawning.computeIfAbsent(worldUUID, (k) -> new MobConfigPerWorld()).setMobSpawning(isSpawningMobs);
        save();
    }

    public synchronized boolean getWorldSpawning(UUID worldUUID) {
        return this.worldSpawning.computeIfAbsent(worldUUID, (k) -> new MobConfigPerWorld()).isMobSpawning();
    }

    public Map<UUID, MobConfigPerWorld> getAllWorlds() {
        return new HashMap<>(this.worldSpawning);
    }
}
