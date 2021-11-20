package voltskiya.apple.game_mechanics.tmw;

import apple.utilities.database.SaveFileable;
import apple.utilities.database.singleton.AppleJsonDatabaseSingleton;
import apple.utilities.util.FileFormatting;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.configs.plugin.manage.functions.ConfigSaveable;
import voltskiya.apple.game_mechanics.util.FileIOService;
import ycm.yml.manager.fields.YcmField;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TmwMobConfigDatabase implements SaveFileable, ConfigSaveable {
    private static AppleJsonDatabaseSingleton<TmwMobConfigDatabase> databaseManager;


    private static TmwMobConfigDatabase instance;
    @YcmField
    public final HashMap<UUID, MobConfigPerWorld> worldSpawning = new HashMap<>();
    @YcmField
    public long regenInterval = 200;
    @YcmField
    public boolean isSpawningMobs = false;

    public TmwMobConfigDatabase() {
        instance = this;
    }

    public static void initialize() {
        databaseManager = new AppleJsonDatabaseSingleton<>(
                PluginTMW.get().getFile(PluginTMW.MOBS_FOLDER),
                FileIOService.get()
        );
    }

    public static TmwMobConfigDatabase loadNow() {
        return databaseManager.loadNow(TmwMobConfigDatabase.class, getFileNameExtStatic());
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

    @Override
    public void saveInstance() {
        save();
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
